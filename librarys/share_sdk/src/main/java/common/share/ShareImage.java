package common.share;

import java.io.Serializable;
import java.util.ArrayList;

public class ShareImage implements Serializable {
	private static final long serialVersionUID = 1820681511810656640L;
	/**
	 * 分享的图片
	 */
	private ArrayList<String> imageUrls = new ArrayList<String>();

	public void addImg(String imgUrl) {
		imageUrls.add(imgUrl);
	}

	public ArrayList<String> getImgUrls() {
		return imageUrls;
	}

}
