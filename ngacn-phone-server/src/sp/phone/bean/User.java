package sp.phone.bean;

import java.util.Set;

public class User {

	// public static final int STATUS_NORMAL = 0;// ����
	// public static final int STATUS_NUKED = 1;// nuked
	// public static final int STATUS_GAG = 2;// ����

	// private long id;
	private String userId;// ���
	// private String userName;// ��¼��
	// private String passWord;// ����
	private String nickName;// �ǳ�
	private String avatarImage;// ͷ��ͼƬ��ַ
	// private int articleNumber;// ������
	// private String signature;// ǩ��
	// private int status;// ״̬
	// private int money;// ��Ǯ
	// private String lastLoginTime;// �������ʱ��
	// private Set<Badge> setBadge;
	// private int level;// �ȼ�
	// private String lastPostTime;// �����ʱ��
	// private int reputation;// ����

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAvatarImage() {
		return avatarImage;
	}

	public void setAvatarImage(String avatarImage) {
		this.avatarImage = avatarImage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
