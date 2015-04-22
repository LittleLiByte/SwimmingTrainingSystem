package com.scnu.swimmingtrainingsystem.util;

/**
 * 保存系统所需常量类
 * 
 * @author LittleByte
 */
public class Constants {

	/**
	 * 超时时间设置
	 */
	public static final int SOCKET_TIMEOUT = 2500;

	/**
	 * 成绩类型 1:普通成绩
	 */
	public static final int NORMALSCORE = 1;
	/**
	 * 成绩类型 2:三次计频成绩
	 */
	public static final int FrequenceSCORE = 2;
	/**
	 * 成绩类型 3:冲刺成绩
	 */
	public static final int SPRINTSCORE = 3;

	/**
	 * SharePreference所用的loginInfo的关键字
	 */
	public static final String LOGININFO = "loginInfo";

	/**
	 * 保存是第几次计时，提醒用户是第几次计时之中
	 */
	public static final String CURRENT_SWIM_TIME = "current";

	/**
	 * 保存所选的计划ID
	 */
	public static final String PLAN_ID = "planID";

	/**
	 * 保存开始计时的日期
	 */
	public static final String TEST_DATE = "testDate";

	/**
	 * 保存手动匹配计时按名次排行的运动员名字,方便除第一趟计时外不用再次拖动运动员进行排行
	 */
	public static final String DRAG_NAME_LIST = "dragList";

	/**
	 * 保存当前登录的用户id
	 */
	public static final String CURRENT_USER_ID = "CurrentUser";

	/**
	 * 保存当前是否可以连接服务器的状态
	 */
	public static final String IS_CONNECT_SERVER = "isConnect";

	/**
	 * 上次登陆的user_id
	 */
	public static final String LAST_LOGIN_USER_ID = "lastLoginUser";

	public static final String COMPLETE_NUMBER = "completeNumber";

	/**
	 * 该用户是否是第一次登陆应用
	 */
	public static final String IS_THIS_USER_FIRST_LOGIN = "isThisUserFirstLogin";

	/**
	 * Log信息所需tag--com.scnu.swimmingtrainingsystem
	 */
	public static final String TAG = "com.scnu.swimmingtrainingsystem";

	/**
	 * 取消
	 */
	public static final String CANCLE_STRING = "取消";

	/**
	 * 确定
	 */
	public static final String OK_STRING = "确定";

	/**
	 * 添加成功
	 */
	public static final String ADD_SUCCESS_STRING = "添加成功";

	public static final String SELECTED_POOL = "selectedPool";

	public static final String SWIM_DISTANCE = "swimDistance";

	public static final String FISRTOPENATHLETE = "fisrtOpenAthlete";

	public static final String FISRTOPENPLAN = "fisrtOpenPlan";

	public static final String FISRTSTARTTIMING = "fisrtStartTiming";

	public static final String CURRENT_DISTANCE = "currentDistance";

	public static final String SCORESJSON = "scoresJson";

	public static final String ATHLETEJSON = "athleteJson";
	/**
	 * 使用说明标题
	 */
	public static final String[] TITLES = new String[] { "1、IP与端口设置说明",
			"2、登录说明", "3、计时器模块说明", "4、小功能模块说明", "5、成绩查询模块" };

	/**
	 * 使用说明内容
	 */
	public static final String[][] CONTENTS = new String[][] {
			{ "•如果要连接服务器，必须先设置服务器的IP和端口地址。服务器局域网IP一般为192.168.x.xxx，端口地址一般为8080.如果尚未建立服务器，"
					+ "可以忽略本条提示" },
			{ "•登录时用户名与密码均不可为空，如果还未注册且服务器尚未建立，可以直接使用【默认帐号】进行登录试用。<br>•如果服务器已经成功开启，可以使用本软"
					+ "件进行注册新帐号，并使用新帐号登录使用。<br>•默认账号只是试用，只有使用注册帐号才可以将数据上传至服务器，进行保存和数据分析。" },
			{ "•要使用本模块，首先就需要录入运动员信息，然后在计时器模块中，在每次计时之前需要选择运动员，并且需要设置泳池大小，游泳的目标距离、游泳计时的间隔距离以及本轮"
					+ "计时的备注，方便区分每次计时成绩。 <br>•在计时页面中，点击计时表盘即可开始，再次点击之后会记录下当前时间， <br>•本趟计时完"
					+ "成之后会跳转到调整页面，对运动员顺序进行调整和根据自己所需删除一些计时时间和运动员，也可直接跳过调整在计完本轮所有成绩时再做总的调整。 "
					+ "<br>•向左或向右可以删除数据，而长按成绩可弹出复制该成绩的对话框<br>•点击右上角的恢复按钮可恢复最初数据" },
			{ "•本模块有三次计频和冲刺计时功能，可自由进行切换，并且支持将结果保存并上传到服务器<br>•向左或向右可以删除数据，而长按成绩可弹出复制该成绩的对话框" },
			{ "•按测试时间查出成绩，可以进行本地查询和联网查询" } };

	public static final String CURRENT_DISTANCE_IS_NULL = "current_distance_is_null";

	public static final String NUMBER_NOT_EQUAL = "number_not_equal";

	public static final String CORRECT = "correct";

	public static final String INTERVAL = "interval";

}
