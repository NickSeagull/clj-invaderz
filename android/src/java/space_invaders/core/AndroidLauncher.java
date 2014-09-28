package space_invaders.core;

import clojure.lang.RT;
import clojure.lang.Symbol;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.Game;

public class AndroidLauncher extends AndroidApplication {
	public void onCreate (android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
          RT.var("clojure.core", "require").invoke(Symbol.intern("space-invaders.core"));
		try {
			Game game = (Game) RT.var("space-invaders.core", "space-invaders").deref();
			initialize(game);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
