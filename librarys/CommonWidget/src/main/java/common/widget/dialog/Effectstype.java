package common.widget.dialog;

import common.widget.dialog.effect.BaseEffects;
import common.widget.dialog.effect.FadeIn;
import common.widget.dialog.effect.Fall;
import common.widget.dialog.effect.FlipH;
import common.widget.dialog.effect.FlipV;
import common.widget.dialog.effect.NewsPaper;
import common.widget.dialog.effect.RotateBottom;
import common.widget.dialog.effect.RotateLeft;
import common.widget.dialog.effect.Shake;
import common.widget.dialog.effect.SideFall;
import common.widget.dialog.effect.SlideBottom;
import common.widget.dialog.effect.SlideLeft;
import common.widget.dialog.effect.SlideRight;
import common.widget.dialog.effect.SlideTop;
import common.widget.dialog.effect.Slit;

public enum Effectstype {

	Fadein(FadeIn.class),
	Slideleft(SlideLeft.class),
	Slidetop(SlideTop.class),
	SlideBottom(SlideBottom.class),
	Slideright(SlideRight.class),
	Fall(Fall.class),
	Newspager(NewsPaper.class),
	Fliph(FlipH.class),
	Flipv(FlipV.class),
	RotateBottom(RotateBottom.class),
	RotateLeft(RotateLeft.class),
	Slit(Slit.class),
	Shake(Shake.class),
	Sidefill(SideFall.class);
	private Class<? extends BaseEffects> effectsClazz;

	private Effectstype(Class<? extends BaseEffects> mclass) {
		effectsClazz = mclass;
	}

	public BaseEffects getAnimator() {
		BaseEffects bEffects = null;
		try {
			bEffects = effectsClazz.newInstance();
		} catch (ClassCastException e) {
			throw new Error("Can not init animatorClazz instance");
		} catch (InstantiationException e) {
			throw new Error("Can not init animatorClazz instance");
		} catch (IllegalAccessException e) {
			throw new Error("Can not init animatorClazz instance");
		}
		return bEffects;
	}
}
