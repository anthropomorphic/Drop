package com.mikefdorst.gdxdemo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mikefdorst.gdxdemo.Drop;

import java.util.Iterator;


public class GameScreen implements Screen {
	private final Drop game;
	private Texture dropImage;
	private Sound dropSound;
	private Array<Rectangle> raindrops;
	private Iterator<Rectangle> raindropIter;
	private Texture bucketImage;
	private Rectangle bucket;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private long lastDropTime;
	
	 public GameScreen(final Drop game) {
		 this.game = game;
		 dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		rainMusic.setLooping(true);
		rainMusic.play();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 450);
		
		touchPos = new Vector3();
		
		batch = new SpriteBatch();
		
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 32;
		bucket.y = 40;
		bucket.width = 64;
		bucket.height = 64;
		
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();
		
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = (int) (touchPos.x - 32);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bucket.x -= 600 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bucket.x += 600 * Gdx.graphics.getDeltaTime();
		}
		if (bucket.x < 0) {
			bucket.x = 0;
		}
		if (bucket.x > 800 - 64) {
			bucket.x = 800 - 64;
		}
		
		if (TimeUtils.timeSinceNanos(lastDropTime) > 1000000000) {
			spawnRaindrop();
		}
		
		raindropIter = raindrops.iterator();
		while (raindropIter.hasNext()) {
			Rectangle raindrop = raindropIter.next();
			raindrop.y -= 300 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0) {
				raindropIter.remove();
			}
			if (raindrop.overlaps(bucket)) {
				dropSound.play();
				raindropIter.remove();
			}
		}
	}
	
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 450;
		raindrop.width = raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public void pause() {
		
	}
	
	@Override
	public void resume() {
		
	}
	
	@Override
	public void hide() {
		
	}
	
	@Override
	public void dispose() {
		dropImage.dispose();
		dropSound.dispose();
		bucketImage.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
