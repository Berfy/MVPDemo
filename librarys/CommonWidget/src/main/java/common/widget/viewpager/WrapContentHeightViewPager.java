package common.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 按内容高度适配的ViewPager
 * 
 * <ol>
 * <li>可按内容高度自适配</li>
 * <li>
 * 如果要按宽度适配，则需要再用其他容器layout包装一下，并设置clipChildren="false"和android:layerType
 * ="software"
 * 
 * <pre class="prettyprint">
 * <LinearLayout 
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:clipChildren="false"
 *     android:layerType="software" >
 * 
 *    <common.widget.viewpager.WrapContentHeightViewPager
 *             android:id="@+id/viewPager"
 *             android:layout_width="match_parent"
 *             android:layout_height="wrap_content"
 *             android:layout_marginLeft="20dp"
 *             android:layout_marginRight="20dp"
 *             android:overScrollMode="never" />
 * 
 * </LinearLayout>
 * </pre>
 * 
 * </li>
 * </ol>
 * 
 * @author zhangquan
 * 
 */
public class WrapContentHeightViewPager extends common.widget.viewpager.ViewPager {

	public WrapContentHeightViewPager(Context context) {
		super(context);
	}

	public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int height = 0;
		// 下面遍历所有child的高度
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (h > height) // 采用最大的view的高度。
				height = h;
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}