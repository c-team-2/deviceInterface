import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import org.OpenNI.*;


public class UniOpenNIDevice extends UniDevice {
	//private static final long serialVersionUID = 1L;
	private OutArg<ScriptNode> scriptNode;
    private Context context;
    private DepthGenerator depthGen;
    private UserGenerator userGen;
    private SkeletonCapability skeletonCap;
    private PoseDetectionCapability poseDetectionCap;
    String calibPose = null;
    
    // These are the relevant data
    // Not everything, look at the get() functions from userGen, depthGen, depthGen.getMetaData(), etc
    int width, height;
    DepthMap depth;
    long depthTimestamp;
    FieldOfView depthFOV;
    int depthFPS;
    DepthMetaData depthMD;
    
    SceneMap scene;	//mask for which pixels are associated to a user?
    int numUsers;
    int users[];
    HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints; //Integer is the User number
    // End relevant data
    
    private final String SAMPLE_XML_FILE = "SamplesConfig.xml";
    
	public UniOpenNIDevice() {
		try {
			scriptNode = new OutArg<ScriptNode>();
			context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);
			
			depthGen = DepthGenerator.create(context);
			DepthMetaData depthMD = depthGen.getMetaData();
			
			width = depthMD.getFullXRes();
			height = depthMD.getFullYRes();
			
			userGen = UserGenerator.create(context);
			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();
			
			userGen.getNewUserEvent().addObserver(new NewUserObserver());
			userGen.getLostUserEvent().addObserver(new LostUserObserver());
			skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
			poseDetectionCap.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
			
			calibPose = skeletonCap.getSkeletonCalibrationPose();
			joints = new HashMap<Integer, HashMap<SkeletonJoint,SkeletonJointPosition>>();
			
			skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
			
			context.startGeneratingAll();
		} catch (GeneralException e) {
			e.printStackTrace();
            System.exit(1);
        }
	}

	void updateAll() throws StatusException {
		updateDepth();
		depthTimestamp = depthGen.getTimestamp();
		depthFOV = depthGen.getFieldOfView();
		
		numUsers = userGen.getNumberOfUsers();
		users = userGen.getUsers();
		for(int i = 0; i < numUsers; ++i) {
			
			getJoints(users[i]);
		}
	}
	
	ByteBuffer getSensorPacket() {
		try {
			updateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create depth channel
		String depthName = "Depth";
		double depthFrequency = (double) depthMD.getFPS();
		UniElementDescriptor[] depthDescriptors = new UniElementDescriptor[1];
		depthDescriptors[0] = new UniElementDescriptor(true, true, true, (byte) depth.getBytesPerPixel());
		long depthNumTuples = depth.getXRes() * depth.getYRes();

		UniChannelHeader depthHeader = new UniChannelHeader(depthNumTuples, depthFrequency, depthDescriptors, depthName);
		// avoiding a copy, just giving empty data here.
		UniChannel depthChannel = new UniChannel(depthHeader, ByteBuffer.allocate(0));
		

		// Sensor header
		short numChannels = 1;
		UniSensorHeader sensorHeader = new UniSensorHeader((byte) 0,(short) 0x045e, (short) 0x02ae, numChannels, 
				System.currentTimeMillis(), 30.0d);
		
		// Calculate sensor packet size
		int capacity = sensorHeader.getPackedSize();
		capacity += depthChannel.getPackedSize();
		
		// Allocate sensor packet
		ByteBuffer sensorPacket = ByteBuffer.allocate(capacity);
		//sensorPacket.order(ByteOrder.LITTLE_ENDIAN);
		
		// Pack sensor header
		sensorHeader.packIntoByteBuffer(sensorPacket);
		
		// Pack depth data
		depthChannel.packIntoByteBuffer(sensorPacket);
		// Pack actual data TODO: (might be little endian)
		DepthMap depthData = depthMD.getData();
		//int size = depthData.getXRes() * depthData.getYRes() * depthData.getBytesPerPixel();
		//ByteBuffer depthSlice = sensorPacket.slice();
		//depthData.copyToBuffer(depthSlice, size);
		for (int x = 0; x < depthData.getXRes(); ++x)
		{
			for (int y = 0; y < depthData.getYRes(); ++y)
			{
				sensorPacket.putShort(depthData.readPixel(x, y));
			}
		}
		
		return sensorPacket;
	}
	
	void updateDepth()
    {
		try {
			context.waitAnyUpdateAll();

            depthMD = depthGen.getMetaData();
            SceneMetaData sceneMD = userGen.getUserPixels(0);
            
            depthFPS = depthMD.getFPS();

            scene = sceneMD.getData();
            depth = depthMD.getData();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }
	
	public void getJoint(int user, SkeletonJoint joint) throws StatusException
    {
        SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user, joint);
		if (pos.getPosition().getZ() != 0)
		{
			HashMap<SkeletonJoint, SkeletonJointPosition> userjoints = joints.get(user);
			if(userjoints != null)
				userjoints.put(joint, new SkeletonJointPosition(depthGen.convertRealWorldToProjective(pos.getPosition()), pos.getConfidence()));
		}
		else
		{
			joints.get(user).put(joint, new SkeletonJointPosition(new Point3D(), 0));
		}
    }
	
    public void getJoints(int user) throws StatusException
    {
    	getJoint(user, SkeletonJoint.HEAD);
    	getJoint(user, SkeletonJoint.NECK);
    	
    	getJoint(user, SkeletonJoint.LEFT_SHOULDER);
    	getJoint(user, SkeletonJoint.LEFT_ELBOW);
    	getJoint(user, SkeletonJoint.LEFT_HAND);

    	getJoint(user, SkeletonJoint.RIGHT_SHOULDER);
    	getJoint(user, SkeletonJoint.RIGHT_ELBOW);
    	getJoint(user, SkeletonJoint.RIGHT_HAND);

    	getJoint(user, SkeletonJoint.TORSO);

    	getJoint(user, SkeletonJoint.LEFT_HIP);
        getJoint(user, SkeletonJoint.LEFT_KNEE);
        getJoint(user, SkeletonJoint.LEFT_FOOT);

    	getJoint(user, SkeletonJoint.RIGHT_HIP);
        getJoint(user, SkeletonJoint.RIGHT_KNEE);
        getJoint(user, SkeletonJoint.RIGHT_FOOT);

    }
    
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("New user " + args.getId());
			try
			{
				if (skeletonCap.needPoseForCalibration())
				{
					poseDetectionCap.startPoseDetection(calibPose, args.getId());
				}
				else
				{
					skeletonCap.requestSkeletonCalibration(args.getId(), true);
				}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class LostUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("Lost user " + args.getId());
			joints.remove(args.getId());
		}
	}
	
	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
	{
		@Override
		public void update(IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args)
		{
			System.out.println("Calibraion complete: " + args.getStatus());
			try
			{
			if (args.getStatus() == CalibrationProgressStatus.OK)
			{
				System.out.println("starting tracking "  +args.getUser());
					skeletonCap.startTracking(args.getUser());
	                joints.put(new Integer(args.getUser()), new HashMap<SkeletonJoint, SkeletonJointPosition>());
			}
			else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT)
			{
				if (skeletonCap.needPoseForCalibration())
				{
					poseDetectionCap.startPoseDetection(calibPose, args.getUser());
				}
				else
				{
					skeletonCap.requestSkeletonCalibration(args.getUser(), true);
				}
			}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
	{
		@Override
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args)
		{
			System.out.println("Pose " + args.getPose() + " detected for " + args.getUser());
			try
			{
				poseDetectionCap.stopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
}
