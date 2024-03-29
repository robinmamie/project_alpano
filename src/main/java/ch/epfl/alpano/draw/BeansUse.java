package ch.epfl.alpano.draw;

import ch.epfl.alpano.gui.PanoramaParametersBean;
import ch.epfl.alpano.gui.PredefinedPanoramas;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public final class BeansUse extends Application {

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final var start = System.nanoTime();
		final var bean = new PanoramaParametersBean(PredefinedPanoramas.NIESEN.parameters());
		final var prop = bean.observerLatitudeProperty();

		prop.addListener((o, oV, nV) -> System.out.printf("  %d -> %d (%s)%n", oV, nV, o));
		System.out.println("set to 1");
		prop.set(1);
		System.out.println("set to 2");
		prop.set(2);
		final var stop = System.nanoTime();
		System.out.printf("Took %.3f ms.%n", (stop - start) * 1e-6);

		Platform.exit();
	}
}