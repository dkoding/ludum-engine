package no.dkit.android.ludum.core.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import no.dkit.android.ludum.core.XXXX;
import no.dkit.android.ludum.core.game.factory.LootFactory;
import no.dkit.android.ludum.core.game.factory.ResourceFactory;
import no.dkit.android.ludum.core.game.model.loot.Loot;
import no.dkit.android.ludum.core.game.model.loot.Weapon;
import no.dkit.android.ludum.core.game.model.world.level.Level;
import no.dkit.android.ludum.core.shaders.AbstractShader;
import no.dkit.android.ludum.core.shaders.AbstractTextureShader;
import no.dkit.android.ludum.core.shaders.texture.TunnellShader;

import java.util.HashMap;
import java.util.List;

public class UpgradeScreenModel {
    Stage stage;
    PlayerData playerData = GameModel.getPlayer().getData();
    Label fpsLabel;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    AbstractShader background;
    private final Table upgradeTable;
    private final Skin skin;
    private final ClickListener populateListener;
    private final ClickListener upgradeListener;

    public UpgradeScreenModel() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        background = new TunnellShader();
        ((AbstractTextureShader)background).init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), ResourceFactory.getInstance().getTexture("stone"));

        spriteBatch = new SpriteBatch();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextureRegion playTexture = ResourceFactory.getInstance().getImage(ResourceFactory.UI, "start");

        final List<Loot.LOOT_TYPE> weaponTypes = Level.getInstance().getWeaponTypes();
        final java.util.Map<Loot.LOOT_TYPE, Weapon> weapons = new HashMap<Loot.LOOT_TYPE, Weapon>();

        for (Loot.LOOT_TYPE weaponType : weaponTypes) {
            weapons.put(weaponType, LootFactory.getInstance().getWeapon(weaponType));
        }

        Weapon currentWeapon = GameModel.getPlayer().getWeapon();

        if (currentWeapon == null)
            currentWeapon = LootFactory.getInstance().getWeapon(weaponTypes.get(0));

        stage = new Stage(new ScreenViewport(camera));

        Button playButton = new Button(new Image(playTexture), skin, "transparent");

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                XXXX.changeScreen(XXXX.SCREEN.AFTER_UPGRADE);
            }
        });

        populateListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Button currentButton = (Button) event.getListenerActor();
                String name = currentButton.getName();
                Weapon weapon = GameModel.getPlayer().getWeapon(Loot.LOOT_TYPE.valueOf(name));
                if(weapon == null)
                    weapon = weapons.get(Loot.LOOT_TYPE.valueOf(name));
                populateUpgradeTable(weapon);
            }
        };

        upgradeListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Button currentButton = (Button) event.getListenerActor();
                String name = currentButton.getName();
                Weapon weapon;
                Weapon playerWeapon = GameModel.getPlayer().getWeapon(Loot.LOOT_TYPE.valueOf(name));

                if(playerWeapon == null)
                    weapon = weapons.get(Loot.LOOT_TYPE.valueOf(name));
                else
                    weapon = playerWeapon;

                if (weapon.canUpgrade() && GameModel.getPlayer().getCredits() > weapon.getUpgradePrice()) {
                    weapon.upgrade();
                    GameModel.getPlayer().removeCredits(weapon.getUpgradePrice());

                    if(playerWeapon == null)
                        weapon.onPickup(GameModel.getPlayer());

                    populateUpgradeTable(weapon);
                }
            }
        };

        fpsLabel = new Label("FPS: ", skin, "halftransparent");

        Table table = new Table(skin);
        table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.defaults().expand().pad(5).center();

        Table resourceTable = new Table(skin);

        Image orbImage = new Image(ResourceFactory.getInstance().getImage(ResourceFactory.ITEM, "orb"));
        Label orbLabel = new Label(""+playerData.getOrbs(), skin, "halftransparent");
        orbLabel.setName("ORBS");
        resourceTable.add(orbImage).center();
        resourceTable.add(orbLabel).center();

        Image creditsImage = new Image(ResourceFactory.getInstance().getImage(ResourceFactory.ITEM, "treasure"));
        Label creditLabel = new Label("" + playerData.getCredits(), skin, "halftransparent");
        orbLabel.setName("CREDITS");
        resourceTable.add(creditsImage).center();
        resourceTable.add(creditLabel).center();

        Table weaponsTable = new Table(skin);
        weaponsTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        weaponsTable.defaults().expand().pad(5).center();

        for (Weapon w : weapons.values()) {
            Button button = new Button(skin);

            Button wb = new Button(new Image(ResourceFactory.getInstance().getWeaponImage(w.getImageName())), skin);
            button.add(wb).fill();
            button.row();

            button.setName(w.getType().toString());
            button.addListener(populateListener);

            weaponsTable.add(button).pad(10).size(200, 200);
        }

        ScrollPane scrollPane = new ScrollPane(weaponsTable);
        scrollPane.setSmoothScrolling(true);

        upgradeTable = new Table(skin);
        upgradeTable.setSize(Gdx.graphics.getWidth(), 200);
        upgradeTable.defaults().pad(5).center();
        upgradeTable.pack();

        populateUpgradeTable(currentWeapon);

        table.add(resourceTable);
        table.row();
        table.add(scrollPane);
        table.row();
        table.add(upgradeTable).fill();
        table.row();
        table.add(playButton).center();
        table.add().row();
        table.add(fpsLabel).colspan(21).center();
        stage.addActor(table);
    }

    private void populateUpgradeTable(Weapon weapon) {
        upgradeTable.clear();

        Label oldTextArea = new Label("Current\n\n" + weapon.getType().toString().replaceAll("_", " ") + " " + weapon.getLevel() + "", skin, "halftransparent");
        oldTextArea.setAlignment(Align.top | Align.left);

        Label newTextArea = new Label("Next\n\n" + weapon.getType().toString().replaceAll("_", " ") + " " + (weapon.getLevel() + 1) + "", skin, "halftransparent");
        newTextArea.setAlignment(Align.top | Align.left);

        Table currentButton = new Table(skin);

        Image currentImage = new Image(ResourceFactory.getInstance().getWeaponImage(weapon.getImageName()));
        currentButton.add(currentImage);
        currentButton.row();

        Label weaponLabel = new Label(weapon.getType().toString().replaceAll("_", " "), skin, "transparent");
        weaponLabel.setAlignment(Align.top | Align.left);
        currentButton.add(weaponLabel);

        Label weaponLevel = new Label("" + weapon.getLevel(), skin, "transparent");
        weaponLevel.setAlignment(Align.top | Align.right);
        currentButton.add(weaponLevel);
        currentButton.row();

        Button upgradeButton = new Button(skin);

        Table newButton = new Table(skin);

        final Image newImage = new Image(ResourceFactory.getInstance().getWeaponImage(weapon.getImageName()));
        newButton.add(newImage);
        newButton.row();

        Label newWeaponLabel = new Label(weapon.getType().toString().replaceAll("_", " "), skin, "transparent");
        newWeaponLabel.setAlignment(Align.center);
        newButton.add(newWeaponLabel);

        Label newWeaponLevel = new Label("" + (weapon.getLevel() + 1), skin, "transparent");
        newWeaponLevel.setAlignment(Align.right);
        newButton.add(newWeaponLevel);

        Label upgradePrice = new Label("BUY UPGRADE!\n>\nPrice: " + weapon.getUpgradePrice(), skin, "transparent");
        upgradePrice.setAlignment(Align.center);
        upgradeButton.add(upgradePrice).fill();

        upgradeButton.setName(weapon.getType().toString());
        upgradeButton.addListener(upgradeListener);

        upgradeTable.add(oldTextArea).fill().expand();
        upgradeTable.add(currentButton).pad(10).size(200, 200);
        upgradeTable.add(upgradeButton).pad(10).size(200, 100);
        upgradeTable.add(newButton).pad(10).size(200, 200);
        upgradeTable.add(newTextArea).fill().expand();
    }

    public Stage getStage() {
        return stage;
    }

    public void update() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawBackground();
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void drawBackground() {
        background.update(0, 0, 0, 0);
        background.render(spriteBatch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        spriteBatch.dispose();
        stage.dispose();
    }
}
