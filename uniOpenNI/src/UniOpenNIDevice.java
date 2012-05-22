import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.HashMap;

import org.OpenNI.*;


public class UniOpenNIDevice extends UniDevice {
	
	ByteBuffer getSensorPacket() {
		try {
			updateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int capacity = 0;
		short numChannels = 0;
		
		// Create depth channel
		String depthName = "Depth";
		double depthFrequency = (double) depthMD.getFPS();
		UniElementDescriptor[] depthDescriptors = new UniElementDescriptor[1];
		depthDescriptors[0] = new UniElementDescriptor(true, true, true, (byte) depth.getBytesPerPixel());
		long depthNumTuples = depth.getXRes() * depth.getYRes();

		UniChannelHeader depthHeader = new UniChannelHeader(depthNumTuples, depthFrequency, depthDescriptors, depthName);
		// avoiding a copy, just giving empty data here.
		UniChannel depthChannel = new UniChannel(depthHeader, ByteBuffer.allocate(0));
		
		// Add depth channel to capacity
		capacity += depthChannel.getPackedSize();
		numChannels++;
		
		
		// Create User1 channel
		String user1Name = "User1";
		double user1Frequency = (double) sceneMD.getFPS();
		UniElementDescriptor[] user1Descriptors = new UniElementDescriptor[4];
		user1Descriptors[0] = new UniElementDescriptor(true, false, true, (byte) 4);
		user1Descriptors[1] = new UniElementDescriptor(true, false, true, (byte) 4);
		user1Descriptors[2] = new UniElementDescriptor(true, false, true, (byte) 4);
		user1Descriptors[3] = new UniElementDescriptor(true, false, true, (byte) 4);
		long user1NumTuples = 15;
		
		UniChannelHeader user1Header = new UniChannelHeader(user1NumTuples, user1Frequency, user1Descriptors, user1Name);
		// avoiding a copy, just giving empty data here.
		UniChannel user1Channel = new UniChannel(user1Header, ByteBuffer.allocate(0));
		
		// Add user1 channel to capacity
		capacity += user1Channel.getPackedSize();
		numChannels++;
		
		// Sensor header
		UniSensorHeader sensorHeader = new UniSensorHeader((byte) 0,(short) 0x045e, (short) 0x02ae, numChannels, 
				System.currentTimeMillis(), 30.0d);
		
		// Add sensor header size to capacity
		capacity += sensorHeader.getPackedSize();
		
		// Allocate sensor packet
		ByteBuffer sensorPacket = ByteBuffer.allocate(capacity);
		
		// Pack sensor header
		sensorHeader.packIntoByteBuffer(sensorPacket);
		
		// Pack depth data
		depthChannel.packIntoByteBuffer(sensorPacket);
		// Pack actual data TODO: probably should get copyToBuffer working
		DepthMap depthData = depthMD.getData();
		ShortBuffer depthBuffer = depthData.createShortBuffer();
		int size = depthData.getXRes() * depthData.getYRes();
		for (int i = 0; i < size; ++i)
		{
			sensorPacket.putShort(depthBuffer.get());
		}
		
		// Pack user1 channel
		user1Channel.packIntoByteBuffer(sensorPacket);
		// Pack actual user1 data
		HashMap<SkeletonJoint, SkeletonJointPosition> user1Skeleton = joints.get(1);
		if (user1Skeleton != null)
		{
			for (int i = 0; i < 15; ++i)
			{
				SkeletonJointPosition pos = joints.get(1).get(jointArray[i]);
				Point3D pos3D = pos.getPosition();
				sensorPacket.putFloat(pos3D.getX());
				sensorPacket.putFloat(pos3D.getY());
				sensorPacket.putFloat(pos3D.getZ());
				sensorPacket.putFloat(pos.getConfidence());
			}
		}
		else
		{
			for (int i = 0; i < 15; ++i)
			{
				sensorPacket.putFloat(0.0f);
				sensorPacket.putFloat(0.0f);
				sensorPacket.putFloat(0.0f);
				sensorPacket.putFloat(0.0f);
			}
		}
		
		return sensorPacket;
	}
	
	public UniOpenNIDevice() {
		try {
			scriptNode = new OutArg<ScriptNode>();
			context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);
			
			depthGen = DepthGenerator.create(context);
			DepthMetaData depthMD = depthGen.getMetaData();
			
			width = depthMD.getFullXRes();
			height = depthMD.getFullYRes();
			
			userGen = UserGenerator.create(context);
			sceneMD = userGen.getUserPixels(0);
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
		context.waitAnyUpdateAll();

        depthMD = depthGen.getMetaData();
        sceneMD = userGen.getUserPixels(0);

        scene = sceneMD.getData();
        depth = depthMD.getData();
		
		numUsers = userGen.getNumberOfUsers();
		users = userGen.getUsers();
		for(int i = 0; i < numUsers; ++i) {
			
			getJoints(users[i]);
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
				skeletonCap.requestSkeletonCalibration(args.getId(), true);
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
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
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
    DepthMetaData depthMD;
    SceneMetaData sceneMD;
    
    SceneMap scene;	//mask for which pixels are associated to a user?
    int numUsers;
    int users[];
    HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints; //Integer is the User number
    // End relevant data
    
    SkeletonJoint jointArray[] = {
    		SkeletonJoint.HEAD,				// 0
    		SkeletonJoint.NECK, 			// 1
    		SkeletonJoint.LEFT_SHOULDER,	// 2
    		SkeletonJoint.LEFT_ELBOW,		// 3
    		SkeletonJoint.LEFT_HAND, 		// 4
    		SkeletonJoint.RIGHT_SHOULDER, 	// 5
    		SkeletonJoint.RIGHT_ELBOW, 		// 6
    		SkeletonJoint.RIGHT_HAND, 		// 7
    		SkeletonJoint.TORSO, 			// 8
    		SkeletonJoint.LEFT_HIP, 		// 9
    		SkeletonJoint.LEFT_KNEE, 		// 10
    		SkeletonJoint.LEFT_FOOT, 		// 11
    		SkeletonJoint.RIGHT_HIP, 		// 12
    		SkeletonJoint.RIGHT_KNEE, 		// 13
    		SkeletonJoint.RIGHT_FOOT};		// 14
    
    private final String SAMPLE_XML_FILE = "SamplesConfig.xml";
}
