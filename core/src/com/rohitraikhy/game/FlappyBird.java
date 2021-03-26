package com.rohitraikhy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture background;
	Texture bird;
	Texture[] birds;
	Circle birdCircle;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	float gravity = 2;
	float gap = 750;
	Texture topTube;
	Texture bottomTube;
	float maxTubeOffset;
	Random randomGenerator;
	Vector2 tubeSize;
	float tubeVelocity = 4;
	int numberofTubes = 4;
	float distanceBetweenTubes;
	float[] tubeX = new float[numberofTubes];
	float[] tubeOffset = new float[numberofTubes];
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;
	Texture gameOver;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("Background.png");
		birds = new Texture[2];
		birds[0] = new Texture("GameScreen/Bird/Bird1.png");
		birds[1] = new Texture("GameScreen/Bird/Bird3.png");
		topTube = new Texture("GameScreen/Pipe.png");
		bottomTube = new Texture("GameScreen/Pipe.png");
		tubeSize = Scaling.fillY.apply(topTube.getWidth(), topTube.getHeight(),
				0, Gdx.graphics.getHeight());
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 200;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberofTubes];
		bottomTubeRectangles = new Rectangle[numberofTubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		gameOver = new Texture("GameOverScreen/GameOver.png");
		startGame();
	}

	/**
	 * Starts the game play.
	 */
	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
		for (int i = 0; i < numberofTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()
					- gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 +
					Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		batch.begin();
		batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (gameState == 1) {
			if (tubeX[scoringTube] < Gdx.graphics.getWidth()) {
				score++;
				if (scoringTube < numberofTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}
			if (Gdx.input.justTouched()) {
				velocity = -30;

			}
			for (int i = 0; i < numberofTubes; i++) {

				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberofTubes * distanceBetweenTubes;
				} else {
					tubeX[i] = tubeX[i] - tubeVelocity;
				}
				tubeX[i] -= tubeVelocity;
				batch.draw(topTube, tubeX[i],
						Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);

				batch.draw(bottomTube, tubeX[i],
						Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight()
								+ tubeOffset[i]);
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2
						+ gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 -
						gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(),
						bottomTube.getHeight());
			}
			if (birdY > 0) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
					Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
			if (Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;

			}
		}
			if (flapState == 0) {
				flapState = 1;
			} else {
				flapState = 0;
			}
			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 -
					birds[flapState].getWidth() / 2, birdY);
			font.draw(batch, String.valueOf(score), 100, 200);
			birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2,
					birds[flapState].getWidth() / 2);
			for (int i = 0; i < numberofTubes; i++) {
				if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
						Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
					gameState = 2;
				}
			}
			batch.end();
		}
	}