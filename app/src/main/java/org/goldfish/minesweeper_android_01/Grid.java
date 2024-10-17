package org.goldfish.minesweeper_android_01;
//Grid.java

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;

/**
 * {@code Grid} 游戏的基本单位 <br/>
 * 用于表示游戏中的一个格子
 *
 */
public class Grid extends AppCompatImageButton {
    //info for controller only
    private final int row;
    private final int col;
    private final Set<Grid> neighbors;
    private final Integer[] surroundingMinesResourceIDs;
    public GameActivity activity;


    private STATE state;
    private boolean mine;
    private int surroundingMines;
    private final int SIZE=100;

    public Grid(GameActivity activity, int row, int col) {
        super(activity);
        this.row = row;
        this.col = col;
        this.mine = false;

        this.state = STATE.CLOSE;
        this.neighbors = new HashSet<>();
        this.surroundingMines = 0;
        this.activity = activity;
        this.surroundingMinesResourceIDs = Resources.drawables;
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ViewGroup.LayoutParams(SIZE, SIZE));
        params.setMargins(2, 2, 2, 2);
        setLayoutParams(params);
        setPadding(0, 0, 0, 0);

        setBackgroundColor(Color.GRAY);

        setAdjustViewBounds(true);

        setOnClickListener(v -> this.activity.getController().generateMine(this));
        updateState();

    }

    public boolean isMine() {
        return mine;
    }

    public STATE getState() {
        return state;
    }

    public Set<Grid> getNeighbors() {
        return neighbors;
    }

    public boolean addNeighbor(Grid neighbor) {
        return neighbors.add(neighbor);
    }

    public boolean setMine() {
        if (mine) return false;
        mine = true;
        surroundingMines = -1;
        updateState();
        return true;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void open() throws MineTriggeredException {
        open(false);
    }

    public void open(boolean reveal) throws MineTriggeredException {
        if (state == STATE.FLAG) {
            return;
        }
        do {
            if (!isMine()) {
                activity.getController().addFinished(this);
                break;
            }
            if (reveal) break;
            throw new MineTriggeredException("Mine triggered.");
        } while (false);

        Log.d("open", toString());
        state = STATE.OPEN;
        activity.getController().addFinished(this);
        updateState();
    }

    public boolean flag() {
        if (state == STATE.OPEN)
            return false;
        switch (state) {
            case FLAG:
                this.state = STATE.CLOSE;
                break;
            case CLOSE:
                state = STATE.FLAG;
                break;
        }
        activity.getController().updateProgress();
        updateState();
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        char state = '-';
        char mine;
        if (this.mine) {
            mine = '#';
        } else {
            mine = (char) ('0' + surroundingMines);
        }
        switch (this.state) {
            case CLOSE:
                state = '-';
            case FLAG:
                state = '>';
            case OPEN:
                state = '+';
        }
        return "" + mine + state;
    }

    public void countSurroundings() {
        surroundingMines = 0;
        for (Grid neighbor : neighbors) {
            if (neighbor.mine) surroundingMines++;
        }
    }

    public int getSurroundingMines() {
        return surroundingMines;
    }

    public void updateState() {
        switch (state) {
            case FLAG:
                setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.flag));
                setBackgroundColor(Color.GRAY);
                return;
            case CLOSE:
                Log.v("Grid::updateState", "Not opened.");
                setImageDrawable(null);
                setBackgroundColor(Color.GRAY);
                return;
        }
        setBackgroundColor(Color.CYAN);
        do {
            if (mine) {
                setImageResource(R.drawable.exploded);
                return;
            }
            if (surroundingMines == 0) return;
        } while (false);

        setImageDrawable(ContextCompat.getDrawable(activity, surroundingMinesResourceIDs[surroundingMines]));
        Log.v("Grid::updateState", "Setting image resource to ImageButton.");
        setBackgroundColor(Color.TRANSPARENT);
        setVisibility(VISIBLE);
        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(SIZE, SIZE);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(viewParams);
        params.setMargins(0, 0, 0, 0);
        setLayoutParams(params);
        setPadding(0, 0, 0, 0);

    }

    public void prepared() {
        setOnLongClickListener((v) -> flag());
        setOnClickListener(new ClickListener());
    }

    public enum STATE {
        CLOSE, FLAG, OPEN
    }


    private class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                activity.getController().open(Grid.this, state == STATE.OPEN);
                activity.getController().updateState();
            }catch (MineTriggeredException e){
                activity.getController().Lose();
            }
        }
    }
}
