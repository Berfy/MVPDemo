package common.widget.textview;

import android.content.Context;
import android.text.method.ReplacementTransformationMethod;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 输入字母自动转换为大写的EditText
 * 
 * @author zhangquan
 * 
 */
public class AutoUpperCaseEditText extends EditText {
	static final char[] lower = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	static final char[] upper = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public AutoUpperCaseEditText(Context context) {
		super(context);
		init();
	}

	public AutoUpperCaseEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AutoUpperCaseEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setTransformationMethod(new ReplacementTransformationMethod() {
			@Override
			protected char[] getOriginal() {
				return lower;
			}

			@Override
			protected char[] getReplacement() {
				return upper;
			}

		});
	}
}
