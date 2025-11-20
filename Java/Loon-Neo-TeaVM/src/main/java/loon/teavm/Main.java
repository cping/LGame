package loon.teavm;

import java.io.IOException;

import org.teavm.vm.TeaVMOptimizationLevel;

import loon.teavm.builder.TargetType;
import loon.teavm.builder.TeaInitialize;
import loon.teavm.make.SkipClass;

@SkipClass
public class Main {

	public static void main(String[] args) {
		try {
			TeaInitialize.create("testing", 800, 600, LauncherMain.class, false, true, TeaVMOptimizationLevel.SIMPLE,
					TargetType.JavaScript);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
