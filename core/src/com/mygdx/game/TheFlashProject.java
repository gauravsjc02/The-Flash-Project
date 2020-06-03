package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;


import java.util.ArrayList;
import java.util.Random;

public class TheFlashProject extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] flash;
	int flashState = 0;
	float gravity = 0.2f;
	float velocity = 0;
	float flashY = 0;
	int score = 0;

	int gameState = 0;
	Rectangle flashRectangle;

	BitmapFont font;

	ArrayList<Integer> boltXs = new ArrayList<Integer>();
	ArrayList<Integer> boltYs = new ArrayList<Integer>();
	ArrayList<Rectangle> boltRectangle = new ArrayList<Rectangle>();
	Texture bolt;
	int boltCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	Texture bomb;
	Texture gameover;
	int bombCount;

	Random random;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("flashBackground.jpg");
		flash = new Texture[12];
		gameover = new Texture("gameover1.png");
		flash[0] = new Texture("frame_00.png");
		flash[1] = new Texture("frame_01.png");
		flash[2] = new Texture("frame_02.png");
		flash[3] = new Texture("frame_03.png");
		flash[4] = new Texture("frame_04.png");
		flash[5] = new Texture("frame_05.png");
		flash[6] = new Texture("frame_06.png");
		flash[7] = new Texture("frame_07.png");
		flash[8] = new Texture("frame_08.png");
		flash[9] = new Texture("frame_09.png");
		flash[10] = new Texture("frame_10.png");
		bolt = new Texture("bolt.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.GOLD);
		font.getData().setScale(10);

		flashY = Gdx.graphics.getHeight() / 2;
	}

	// method to generate bolts at random positions
	public void makeBolt(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		boltYs.add((int)height);
		boltXs.add(Gdx.graphics.getWidth());
	}

	// method to generate bombs at random positions
	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


		if(gameState == 1){
			// Game is live

			// Bombs
			if(bombCount < 250){
				bombCount ++;
			}else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangle.clear();
			for(int i = 0;i < bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 8);
				bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}


			// Bolts
			if(boltCount<100){
				boltCount ++;
			}else {
				boltCount = 0;
				makeBolt();
			}

			boltRectangle.clear();
			for(int i = 0;i < boltXs.size();i++){
				batch.draw(bolt,boltXs.get(i),boltYs.get(i));
				boltXs.set(i, boltXs.get(i) - 4);
				boltRectangle.add(new Rectangle(boltXs.get(i), boltYs.get(i),bolt.getWidth(),bolt.getHeight()));
			}

			if(Gdx.input.justTouched()){
				velocity = -10;
			}

			if(flashState<10){
				flashState++;
			}else {
				flashState = 0;
			}

			velocity += gravity;
			flashY -= velocity;

			if(flashY <= 0){
				flashY = 0;
			}
			if(flashY >= Gdx.graphics.getHeight() - flash[flashState].getHeight()){
				flashY = Gdx.graphics.getHeight() - flash[flashState].getHeight();
			}

		}else if(gameState == 0){
			// Waiting to start
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}else if(gameState == 2){
			// Game over
			if(Gdx.input.justTouched()){
				gameState = 1;
				flashY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				boltXs.clear();
				boltYs.clear();
				boltRectangle.clear();
				boltCount=0;
				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount=0;
			}
		}


		if(gameState == 2){
			batch.draw(gameover, Gdx.graphics.getWidth()/3,Gdx.graphics.getHeight()/2 );
		}else{
			batch.draw(flash[flashState],Gdx.graphics.getWidth()/6 - flash[flashState].getWidth()/2,flashY);
		}

		flashRectangle = new Rectangle(Gdx.graphics.getWidth()/6 - flash[flashState].getWidth(), (int) flashY ,flash[flashState].getWidth(),flash[flashState].getHeight());
		for (int i =0;i< boltRectangle.size();i++){
			if(Intersector.overlaps(flashRectangle, boltRectangle.get(i))){
				Gdx.app.log("Coin","Collision!");
				score++;

				boltRectangle.remove(i);
				boltXs.remove(i);
				boltYs.remove(i);
				break;
			}
		}

		for (int i =0;i< bombRectangle.size();i++){
			if(Intersector.overlaps(flashRectangle, bombRectangle.get(i))){
				Gdx.app.log("Bomb","Collision!");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score),1800,1000);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
