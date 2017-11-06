
package br.cefetmg.games.Transition;

import br.cefetmg.games.screens.BaseScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Timer;
import java.util.ArrayList;

public class TransitionScreen extends ScreenAdapter {
    
    public enum Effect {
        NONE,
        FADE_IN,
        FADE_OUT,
        FADE_IN_OUT
    }
    
    private static TransitionScreen lastInstance = null;
    private Game game;
    private BaseScreen current;
    private BaseScreen next;
    private ArrayList<TransitionEffect> transitionEffects;
    private int currentTransitionEffect;

    // ======== Construtores ======== //
    
    private TransitionScreen(BaseScreen current, BaseScreen next, ArrayList<TransitionEffect> transitionEffects) {
        this.current = current;
        this.next = next;
        this.transitionEffects = transitionEffects;
        this.currentTransitionEffect = 0;
        this.game = current.game;
    }
    
    private TransitionScreen(BaseScreen current, BaseScreen next) {
        this.current = current;
        this.next = next;
        this.transitionEffects = new ArrayList<TransitionEffect>();
        this.currentTransitionEffect = 0;
        this.game = current.game;
    }
    
    private TransitionScreen(BaseScreen current) {
        this.current = current;
        this.next = current;
        this.transitionEffects = new ArrayList<TransitionEffect>();
        this.currentTransitionEffect = 0;
        this.game = current.game;
    }
    
    public static TransitionScreen getInstance(BaseScreen current, BaseScreen next, ArrayList<TransitionEffect> transitionEffects) {
        if (lastInstance == null)
            return new TransitionScreen(current, next, transitionEffects);
        else
            return lastInstance;
    }
    
    public static TransitionScreen getInstance(BaseScreen current, BaseScreen next) {
        if (lastInstance == null)
            return new TransitionScreen(current, next);
        else
            return lastInstance;
    }
    
    public static TransitionScreen getInstance(BaseScreen current) {
        if (lastInstance == null)
            return new TransitionScreen(current);
        else
            return lastInstance;
    }
    
    // ================ //

    @Override
    public void render(float delta) {
        if (currentTransitionEffect >= transitionEffects.size()) {
            game.setScreen(next);
            lastInstance = null;
            return;
        }

        transitionEffects.get(currentTransitionEffect).update(delta);
        transitionEffects.get(currentTransitionEffect).render(current);

        if (transitionEffects.get(currentTransitionEffect).isFinished())
            currentTransitionEffect++;
    }
    
    private void setTask(Timer.Task task) {
        if (task != null)
            transitionEffects.add(new ActionsTransitionEffect(task));
        else if (current != next) {
            transitionEffects.add(new ActionsTransitionEffect(new Timer.Task() {
                @Override
                public void run() {
                    current = next;
                }
            }));
        }
    }
    
    public void execute(Effect effect, float duration, Timer.Task task) {
        if (lastInstance != null)
            return;
        
        lastInstance = this;
        
        switch (effect) {
            case FADE_IN:
                transitionEffects.add(new FadeInTransitionEffect(duration));
                setTask(task);
            break;
            case FADE_IN_OUT:
                transitionEffects.add(new FadeInTransitionEffect(duration));
            case FADE_OUT:
                setTask(task);
                transitionEffects.add(new FadeOutTransitionEffect(duration));
            break;
            default:
                setTask(task);
            break;
        }
        
        game.setScreen(this);
    }
    
    public void execute(Effect effect, float duration) {
        execute(effect, duration, null);
    }
    
    public void execute() {
        if (transitionEffects != null && !transitionEffects.isEmpty())
            game.setScreen(this);
    }
}
