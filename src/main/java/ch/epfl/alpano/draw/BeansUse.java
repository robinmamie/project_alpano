package ch.epfl.alpano.draw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.gui.PanoramaParametersBean;
import ch.epfl.alpano.gui.PredefinedPanoramas;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public final class BeansUse extends Application {

	private static final Logger logger = LogManager.getLogger(BeansUse.class);

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final var start = System.nanoTime();
		final var bean = new PanoramaParametersBean(PredefinedPanoramas.NIESEN.parameters());
		final var prop = bean.observerLatitudeProperty();

		prop.addListener((o, oV, nV) -> logger.info("  {} -> {} ({})", oV, nV, o));
		logger.info("set to 1");
		prop.set(1);
		logger.info("set to 2");
		prop.set(2);
		final var stop = System.nanoTime();
		logger.info("Took {} ms.", (stop - start) * 1e-6);

		Platform.exit();
	}
}