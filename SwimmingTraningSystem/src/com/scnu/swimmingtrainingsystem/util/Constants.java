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
	public static final int SOCKET_TIMEOUT = 1500;

	/**
	 * SharePreference所用的loginInfo的关键字
	 */
	public static final String LOGININFO = "loginInfo";

	/**
	 * 游泳趟数，用于控制跳转
	 */
	public static final String SWIM_TIME = "swimTime";

	/**
	 * 保存是第几次计时，提醒用户是第几次计时之中
	 */
	public static final String CURRENT_SWIM_TIME = "current";

	/**
	 * 保存所选计划中多有少个运动员，用于秒表的点击次数
	 */
	public static final String ATHLETE_NUMBER = "athleteCount";

	/**
	 * 保存所选计划中的运动院ID list
	 */
	public static final String ATHLTE_ID_LIST = "athIDList";

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
	public static final String IS_CONNECT_SERVICE = "isConnect";
	
	/**
	 * 上次登陆的user_id
	 */
	public static final String LAST_LOGIN_USER_ID="lastLoginUser";
	
	/**
	 * 该用户是否是第一次登陆应用
	 */
	public static final String IS_THIS_USER_FIRST_LOGIN="isThisUserFirstLogin";

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

	public final static String countries[] = new String[] { "第1道", "第2道",
			"第3道", "第4道", "第5道", "第6道", "第7道", "第8道", };

	public static final String FISRTOPENATHLETE = "fisrtOpenAthlete";

	public static final String FISRTOPENPLAN = "fisrtOpenPlan";
	/**
	 * 使用说明标题
	 */
	public static final String[] TITLES = new String[] { "1、IP与端口设置说明",
			"2、登录说明", "3、	计时器模块说明", "4、两种计时方式说明", "5、成绩查询模块" };

	/**
	 * 使用说明内容
	 */
	public static final String[][] CONTENTS = new String[][] {
			{ "如果要连接服务器，必须先设置服务器的IP和端口地址。服务器局域网IP一般为192.168.x.xxx，端口地址一般为8080.如果尚未建立服务器，"
					+ "可以忽略本条提示" },
			{ "登录时用户名与密码均不可为空，如果还未注册且服务器尚未建立，可以直接使用【默认帐号】进行登录试用。如果服务器已经成功开启，可以使用本软"
					+ "件进行注册新帐号，并使用新帐号登录使用。而默认账号只是试用，只有使用注册帐号才可以将数据上传至服务器，进行保存和数据分析。" },
			{ "要使用计时器模块，首先要存在计划，而要新建计划，首先就需要有运动员。因此流程为：在【运动员模块】新建运动员，然后在【计划模块】设置泳池大小和游泳"
					+ "趟数，以及选择运动员加入计划中，然后在【计时器模块】中，在每次计时之前需要选择计划，然后才能开始计时。" },
			{ "【分泳道计时】：点击对应泳道可以设置该泳道为第几道和使用该泳道的运动员，为了照顾大多数手机，因此设置了最多八道泳道。点击最上方处文字"
					+ "说明即可开始计时，点击对应泳道区域表示该泳道的运动员到达终点，并显示其成绩。当全部运动员计时完毕，会弹出操作提示对话框，"
					+ "可以选择重置本次计时成绩或者保存并进行下一次计时。当所有趟次计时都完毕了，会显示多次成绩以及这几次的综合统计成绩\n"
					+ "【手动匹配计时】：点击上方表盘区域即可开始计时，开始后，每个运动员到达终点时点击一次表盘，即会记录运动员到达终点的成绩，"
					+ "全部运动员到达终点后，点击底部的运动员成绩匹配按钮跳转到匹配页面。在成绩匹配页面，拖动运动员名字到对应的成绩的行，"
					+ "最后按下保存按钮即可保存本次计时。同样，计时完成后会显示每次的成绩和综合成绩。" },
			{ "成绩查询时可以按照进行训练测试日期进行查询" } };

}
