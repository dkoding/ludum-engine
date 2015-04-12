package no.dkit.android.ludum.core.game.model.world.map.featureobstacle;

public class FeatureMap {
    public FeatureMap(Feature[][] feature, int[][] tiles, int[][] items) {
        for (int r = 0; r < Feature.FEATURE_ROWS; r++)
            for (int c = 0; c < Feature.FEATURE_COLS; c++) {
                for (int nr = 0; nr < Feature.FEATURE_NUM_ROWS; nr++)
                    for (int nc = 0; nc < Feature.FEATURE_NUM_COLS; nc++) {
                        tiles[(c * Feature.FEATURE_NUM_COLS) + nc]
                                [(r * Feature.FEATURE_NUM_ROWS) + nr] = feature[c][r].tiles[nc][nr];
                    }
            }

        for (int r = 0; r < Feature.FEATURE_ROWS; r++)
            for (int c = 0; c < Feature.FEATURE_COLS; c++) {
                for (int nr = 0; nr < Feature.FEATURE_NUM_ROWS; nr++)
                    for (int nc = 0; nc < Feature.FEATURE_NUM_COLS; nc++) {
                        items[(c * Feature.FEATURE_NUM_COLS) + nc]
                                [(r * Feature.FEATURE_NUM_ROWS) + nr] = feature[c][r].items[nc][nr];
                    }
            }
    }
}
