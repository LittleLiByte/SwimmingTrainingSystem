package com.example.swimmingtraningsystem.util;

/**
 * ����ϵͳ���賣����
 * 
 * @author LittleByte
 */
public class Constants {

	/**
	 * ��ʱʱ������
	 */
	public static final int SOCKET_TIMEOUT = 1500;

	/**
	 * SharePreference���õ�loginInfo�Ĺؼ���
	 */
	public static final String LOGININFO = "loginInfo";

	/**
	 * ��Ӿ���������ڿ�����ת
	 */
	public static final String SWIM_TIME = "swimTime";

	/**
	 * �����ǵڼ��μ�ʱ�������û��ǵڼ��μ�ʱ֮��
	 */
	public static final String CURRENT_SWIM_TIME = "current";

	/**
	 * ������ѡ�ƻ��ж����ٸ��˶�Ա����������ĵ������
	 */
	public static final String ATHLETE_NUMBER = "athleteCount";

	/**
	 * ������ѡ�ƻ��е��˶�ԺID list
	 */
	public static final String ATHLTE_ID_LIST = "athIDList";

	/**
	 * ������ѡ�ļƻ�ID
	 */
	public static final String PLAN_ID = "planID";

	/**
	 * ���濪ʼ��ʱ������
	 */
	public static final String TEST_DATE = "testDate";

	/**
	 * �����ֶ�ƥ���ʱ���������е��˶�Ա����,�������һ�˼�ʱ�ⲻ���ٴ��϶��˶�Ա��������
	 */
	public static final String DRAG_NAME_LIST = "dragList";

	/**
	 * ���浱ǰ��¼���û�id
	 */
	public static final String CURRENT_USER_ID = "CurrentUser";

	/**
	 * ���浱ǰ�Ƿ�������ӷ�������״̬
	 */
	public static final String IS_CONNECT_SERVICE = "isConnect";

	/**
	 * Log��Ϣ����tag--com.example.swimmingtraningsystem
	 */
	public static final String TAG = "com.example.swimmingtraningsystem";

	/**
	 * ȡ��
	 */
	public static final String CANCLE_STRING = "ȡ��";

	/**
	 * ȷ��
	 */
	public static final String OK_STRING = "ȷ��";

	/**
	 * ���ӳɹ�
	 */
	public static final String ADD_SUCCESS_STRING = "���ӳɹ�";

	public final static String countries[] = new String[] { "��1��", "��2��",
			"��3��", "��4��", "��5��", "��6��", "��7��", "��8��", };
	/**
	 * ʹ��˵������
	 */
	public static final String[] TITLES = new String[] { "1��IP��˿�����˵��",
			"2����¼˵��", "3��	��ʱ��ģ��˵��", "4�����ּ�ʱ��ʽ˵��", "5���ɼ���ѯģ��" };

	/**
	 * ʹ��˵������
	 */
	public static final String[][] CONTENTS = new String[][] {
			{ "���Ҫ���ӷ����������������÷�������IP�Ͷ˿ڵ�ַ��������������IPһ��Ϊ192.168.x.xxx���˿ڵ�ַһ��Ϊ8080.�����δ������������"
					+ "���Ժ��Ա�����ʾ" },
			{ "��¼ʱ�û��������������Ϊ�գ������δע���ҷ�������δ����������ֱ��ʹ�á�Ĭ���ʺš����е�¼���á�����������Ѿ��ɹ�����������ʹ�ñ���"
					+ "������ע�����ʺţ���ʹ�����ʺŵ�¼ʹ�á���Ĭ���˺�ֻ�����ã�ֻ��ʹ��ע���ʺŲſ��Խ������ϴ��������������б�������ݷ�����" },
			{ "Ҫʹ�ü�ʱ��ģ�飬����Ҫ���ڼƻ�����Ҫ�½��ƻ������Ⱦ���Ҫ���˶�Ա���������Ϊ���ڡ��˶�Աģ�顿�½��˶�Ա��Ȼ���ڡ��ƻ�ģ�顿����Ӿ�ش�С����Ӿ"
					+ "�������Լ�ѡ���˶�Ա����ƻ��У�Ȼ���ڡ���ʱ��ģ�顿�У���ÿ�μ�ʱ֮ǰ��Ҫѡ��ƻ���Ȼ����ܿ�ʼ��ʱ��" },
			{ "����Ӿ����ʱ���������ӦӾ���������ø�Ӿ��Ϊ�ڼ�����ʹ�ø�Ӿ�����˶�Ա��Ϊ���չ˴�����ֻ���������������˵�Ӿ����������Ϸ�������"
					+ "˵�����ɿ�ʼ��ʱ�������ӦӾ�������ʾ��Ӿ�����˶�Ա�����յ㣬����ʾ��ɼ�����ȫ���˶�Ա��ʱ��ϣ��ᵯ��������ʾ�Ի���"
					+ "����ѡ�����ñ��μ�ʱ�ɼ����߱��沢������һ�μ�ʱ���������˴μ�ʱ������ˣ�����ʾ��γɼ��Լ��⼸�ε��ۺ�ͳ�Ƴɼ�\n"
					+ "���ֶ�ƥ���ʱ��������Ϸ��������򼴿ɿ�ʼ��ʱ����ʼ��ÿ���˶�Ա�����յ�ʱ���һ�α��̣������¼�˶�Ա�����յ�ĳɼ���"
					+ "ȫ���˶�Ա�����յ�󣬵���ײ����˶�Ա�ɼ�ƥ�䰴ť��ת��ƥ��ҳ�档�ڳɼ�ƥ��ҳ�棬�϶��˶�Ա���ֵ���Ӧ�ĳɼ����У�"
					+ "����±��水ť���ɱ��汾�μ�ʱ��ͬ������ʱ��ɺ����ʾÿ�εĳɼ����ۺϳɼ���" },
			{ "�ֽ׶ο��Խ��а��˶�Ա���ֲ���ɼ�������������������Խ���������ѯ��" } };
}