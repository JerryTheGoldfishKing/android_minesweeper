package org.goldfish.minesweeper_android_01;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;

public class Controller {
    public static String thrower = "GOLDFISH_CAUGHT";

    private final int height, width, mines;
    Grid[][] grids;
    Set<Grid> finishedGrids;
    GameActivity activity;
    Chronometer chronometer;
    private int used;
    private boolean finished;

    public boolean isFinished() {
        return finished;
    }

    public Controller(int height, int width, int mines) {
        this.height = height;
        this.width = width;
        this.used = 0;
        this.mines = mines;
        this.grids = new Grid[height][width];
        this.finished = false;
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

    public void add(Grid grid) {
        if (used >= height * width) throw new RuntimeException("Array Already Full");
        grids[used / width][used % width] = grid;
        used++;
    }

    public void findSurroundings() {
        if (used < width * height) throw new RuntimeException("Array Not Full");
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Grid grid = grids[row][col];
                //在九宫格范围内寻找相邻格
                for (int d_row = -1; d_row <= 1; d_row++) {
                    for (int d_col = -1; d_col <= 1; d_col++) {
                        if (d_row == 0 && d_col == 0) continue;
                        //起点格坐标

                        int neighbor_candidate_row = row + d_row;
                        int neighbor_candidate_col = col + d_col;
                        //候选坐标生成

                        if (neighbor_candidate_row < 0) continue;//超上界
                        if (neighbor_candidate_row >= height) continue;//超下界
                        if (neighbor_candidate_col < 0) continue;//超左界
                        if (neighbor_candidate_col >= width) continue;//超右界

                        Grid gridCandidate = grids[neighbor_candidate_row][neighbor_candidate_col];
                        if (gridCandidate == null) {
                            String messageBuf = "(" + neighbor_candidate_row + "," + neighbor_candidate_col + ')';
                            Log.w("GOLDFISH_SELF_CAUGHT", messageBuf);
                            return;
                        }

                        if (grid.addNeighbor(gridCandidate)) continue;

                        //添加邻居不成功 则执行以下处理
                        if (activity == null)
                            throw new NullPointerException("Call SetActivity first");
                        Toast.makeText(activity, "邻接错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    }

    public Grid getGrid(int index) {
        return getGrid(index / width, index % width);
    }

    public Grid getGrid(int row, int col) {
        try {
            return grids[row][col];
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
        if (chronometer == null) {
            Log.w(thrower, "setChronometer: ", new NullPointerException());
            return;
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void generateMine(Grid start) {
        Log.i("Controller:generateMine", "generateMine: " + "<" + start.getRow() + '-' + start.getCol() + '>');
        Set<Grid> invalidGrids = new LinkedHashSet<>();
        invalidGrids.add(start);
        invalidGrids.addAll(start.getNeighbors());

        finishedGrids = new LinkedHashSet<>();
        finishedGrids.add(start);
        finishedGrids.addAll(start.getNeighbors());


        for (int i = 0; i < mines; ) {
            int h = (int) (Math.random() * height);
            int w = (int) (Math.random() * width);
            boolean exist = false;

            for (Grid grid : invalidGrids) {
                if (h == grid.getRow() && w == grid.getCol()) {
                    exist = true;
                    break;
                }
            }
            if (exist) continue;

            //loop exited because loc generated cannot be set mine
            //so just try generating another one
            Grid candidateMinedGrid = getGrid(h, w);
            if (candidateMinedGrid == null) {
                Log.w(Controller.thrower, "Controller:generateMine: " + h + ',' + w);
                return;
            }
            if (candidateMinedGrid.setMine()) {
                invalidGrids.add(candidateMinedGrid);
                i++;
            }
        }


        for (int index = 0; index < width * height; index++) {
            Log.v("Controller:generateMine", "UPDATE");
            getGrid(index).countSurroundings();
        }

        open(start);
        for (int index = 0; index < width * height; index++) {
            getGrid(index).updateState();
            getGrid(index).prepared();
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

    }

    @NonNull
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (Grid[] row : grids) {
            for (Grid grid : row)
                buffer.append(grid).append(' ');
            buffer.append('\n');
        }
        return buffer.toString();
    }

    public void open(Grid start) {
        open(start, false);
    }

    public void open(Grid start, boolean started) {
        Queue<Grid> queue = new LinkedList<>();
        boolean[][] visited = new boolean[height][width];
        if (finished) {
            Toast.makeText(activity, "游戏已结束", Toast.LENGTH_SHORT).show();
            return;
        }
        if (started) {
            if (start.getState() == Grid.STATE.FLAG) return;
            int flagCount = 0;
            //打开这一格周围没插旗的格子
            for (Grid n : start.getNeighbors()) {
                switch (n.getState()) {
                    case FLAG:
                        flagCount++;
                    case OPEN:
                        continue;
                    default:
                        break;
                }
                queue.add(n);
            }
            int remaining = start.getSurroundingMines() - flagCount;
            String message;
            if (remaining > 0) {
                message = String.format(Locale.CHINA, "少插了%d个旗子", remaining);
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                return;
            }
            if (remaining < 0) {
                message = String.format(Locale.CHINA, "多插了%d个旗子", -remaining);
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            queue.add(start);
        }
        while (!queue.isEmpty()) {
            Grid current = queue.poll();
            if (current == null) {
                Log.w("Controller:open", "Null stored in neighbor");
                continue;
            }
            Log.i("Controller:open", "Locking mutex for grid: " + current);
            current.open(); // Check if this leads to deadlocks or accesses destroyed objects

            visited[current.getRow()][current.getCol()] = true;
            if (current.getSurroundingMines() != 0) continue;

            for (Grid potentials : current.getNeighbors()) {
                if (visited[potentials.getRow()][potentials.getCol()]) continue;
                queue.add(potentials);
            }
            System.out.println(activity.getController());
        }
    }

    public void addFinished(Grid grid) {
        switch (grid.getState()) {
            case FLAG:
                if (!grid.isMine()) return;
                break;
            case OPEN:
                if (grid.isMine()) return;
                break;
        }
        finishedGrids.add(grid);
        checkFinished();
        updateProgress();
    }

    public int countFlagTotal() {
        int count = 0;
        for (Grid[] row : grids) {
            for (Grid g : row) {
                if (g.getState() == Grid.STATE.FLAG) count++;
            }
        }
        return count;
    }

    public void updateProgress() {
        activity.getMinePrompt().setText(String.valueOf(mines - countFlagTotal()));
    }


    //结算
    private void checkFinished() {
        for (Grid[] row : grids) {
            for (Grid grid : row) {
                switch (grid.getState()) {
                    case OPEN:
                        if (!grid.isMine())
                            continue;
                        Lose();
                        return;
                    default:
                        if (!grid.isMine()) return;
                        break;
                }
            }
        }
        this.finished = true;
        dialog(true).show();
    }

    void Lose() {
        this.finished = true;
        reveal();
        dialog(false).show();
    }

    void updateState() {
        for (Grid[] row : grids) {
            for (Grid g : row)
                g.updateState();
        }
    }

    void reveal() {
        for (Grid[] row : grids) {
            for (Grid g : row) {
                g.open(true);
                g.updateState();
            }
        }
    }

    AlertDialog dialog(boolean win) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String title = win ? "您赢了" : "您输了";
        chronometer.stop();
        long timeUsed = chronometer.getDrawingTime() - chronometer.getBase();
        title += '\n' + timeUsed;


        builder.setTitle(title);
        builder.setPositiveButton("确认",
                (dialog, which) -> promptAndExit()
        );
        builder.setNegativeButton("查看结果",
                (dialog, which) -> activity.addExitButton()
        );

        return builder.create();
    }

    void promptAndExit() {
        Toast.makeText(activity, "下次扫雷再见！", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }
}

