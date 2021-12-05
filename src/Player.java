import ui.AddSongWindow;
import ui.PlayerWindow;

import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Player {
    AddSongWindow addSongWindow;
    PlayerWindow playerWindow;
    String[][] playerQueue = new String[5][7]; // Instantiating for debugging
    SongPlayingThread sp_thread;
    String[] currentSong;
    boolean isPlaying = false;
    int counter = 1;
    int resumeTime;
    private int currentSongQueueId;
    private int new_time;
    private final Lock thread = new ReentrantLock();

    public Player() {
        ActionListener buttonListenerPlayNow =  e -> start();
        ActionListener buttonListenerRemove =  e -> remove();
        ActionListener buttonListenerAddSong =  e -> add();
        ActionListener buttonListenerPlayPause =  e -> playPause();
        ActionListener buttonListenerStop =  e -> stop();
        ActionListener buttonListenerNext =  e -> next();
        ActionListener buttonListenerPrevious =  e -> previous();
        ActionListener buttonListenerShuffle =  e -> shuffle();
        ActionListener buttonListenerRepeat =  e -> repeat();

        // Setting initial songs for easier debugging
        for (int i = 0; i < 5; i++){
            this.playerQueue[i][0] = "Title " + i;
            this.playerQueue[i][1] = "Album " + i;
            this.playerQueue[i][2] = "Artist " + i;
            this.playerQueue[i][3] = "2020";
            this.playerQueue[i][4] = "00:05:00";
            this.playerQueue[i][5] = "300";
            this.playerQueue[i][6] = String.valueOf(this.counter);
            this.counter++;
        }


        MouseListener scrubberListenerClick = new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e){
                click_up();
            }

            @Override
            public void mousePressed(MouseEvent e){
                click_down();
            }

            @Override
            public void mouseEntered(MouseEvent e){}

            @Override
            public void mouseExited(MouseEvent e){}
        };

        MouseMotionListener scrubberListenerMotion = new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e) {
                drag();
            }

            @Override
            public void mouseMoved(MouseEvent e){

            }

        };

        String windowTitle = "Player de Música";

        this.playerWindow = new PlayerWindow(
                buttonListenerPlayNow,
                buttonListenerRemove,
                buttonListenerAddSong,
                buttonListenerPlayPause,
                buttonListenerStop,
                buttonListenerNext,
                buttonListenerPrevious,
                buttonListenerShuffle,
                buttonListenerRepeat,
                scrubberListenerClick,
                scrubberListenerMotion,
                windowTitle,
                this.playerQueue
        );


    }

    private String createID() {
        String current_id = String.valueOf(this.counter);
        this.counter++;
        return  current_id;
    }


    private void repeat() {
    }

    private void shuffle() {
    }

    private void changeSong(int idQueue){
        try{
            this.thread.lock();
            String[] selectedSong = this.playerQueue[idQueue];
            this.currentSongQueueId = idQueue;
            this.sp_thread.interrupt();
            this.sp_thread = new SongPlayingThread(this.playerWindow, Integer.parseInt(selectedSong[5]),
                    0, this.currentSongQueueId, this.playerQueue.length);
            playerWindow.updatePlayingSongInfo(selectedSong[0], selectedSong[1], selectedSong[2]);
            this.currentSong = selectedSong;

            SwingUtilities.invokeLater(() -> {
                this.playerWindow.queuePanel.queueList.setRowSelectionInterval(this.currentSongQueueId,
                                                                                this.currentSongQueueId);
            });
            this.sp_thread.start();
        }
        finally {
            this.thread.unlock();
        }
    }

    private void previous() {
        Thread t_previous = new Thread(() -> changeSong(this.currentSongQueueId - 1));
        t_previous.start();
    }



    private void next() {
        Thread t_next  = new Thread(() -> changeSong(this.currentSongQueueId + 1));

        t_next.start();
    }



    private void stop() {
    }
    private void click_up() {
        Thread t_click_up = new Thread(() -> {
            try{
                this.thread.lock();
                this.sp_thread = new SongPlayingThread(this.playerWindow, Integer.parseInt(this.currentSong[5]), this.new_time
                        ,this.currentSongQueueId, this.playerQueue.length);
                // System.out.println(this.playerWindow.getScrubberValue());
                this.sp_thread.start();
            }
            finally {
                this.thread.unlock();
            }
        });

        t_click_up.start();


    }
    private void click_down() {
        //System.out.println("mouse down");
        Thread t_click_down = new Thread(() -> {
            try{
                this.thread.lock();
                if (this.sp_thread != null && !this.sp_thread.isInterrupted()){
                    this.sp_thread.interrupt();
                    this.sp_thread = null;

                    this.new_time = this.playerWindow.getScrubberValue();
                    // System.out.println("should update to " + this.new_time);
                    drag();
                    this.playerWindow.updateMiniplayer(true,true,false,this.new_time,
                            Integer.parseInt(this.currentSong[5]), this.currentSongQueueId, this.playerQueue.length);
                }
            }
            finally {
                this.thread.unlock();
            }
        });
        t_click_down.start();


    }

    private void drag() {
        Thread t_drag = new Thread(() -> {
            try{
                this.thread.lock();
                int value = this.playerWindow.getScrubberValue();
                // System.out.println("Dragged " + value);
                this.playerWindow.updateMiniplayer(true,true,false,value,
                        Integer.parseInt(this.currentSong[5]), this.currentSongQueueId, this.playerQueue.length);
                this.new_time = playerWindow.getScrubberValue();
            }
            finally {
                this.thread.unlock();
            }
        });

        t_drag.start();

    }

    private void playPause() {
        Thread t_toggle_play = new Thread(() -> {
            try{
                this.thread.lock();
                if (this.isPlaying){
                    this.resumeTime = this.playerWindow.getScrubberValue() + 1;
                    this.sp_thread.interrupt();
                    this.playerWindow.updatePlayPauseButton(false);
                    this.isPlaying = false;
                }
                else {
                    this.sp_thread = new SongPlayingThread(this.playerWindow, Integer.parseInt(this.currentSong[5]),
                            this.resumeTime, this.currentSongQueueId, this.playerQueue.length );
                    this.sp_thread.start();
                    this.playerWindow.updatePlayPauseButton(true);
                    this.isPlaying = true;
                }
            }
            finally {
                this.thread.unlock();
            }
        });

        t_toggle_play.start();


    }

    private void add() {
        Thread t_add = new Thread(() -> {
            ActionListener addSongOk = e-> {
                try{
                    this.thread.lock();
                    int queueLength = this.playerQueue != null ? this.playerQueue.length : 0;
                    String[] song = this.addSongWindow.getSong();
                    String[][] newQueue = new String[queueLength + 1][7];
                    for (int i = 0; i < queueLength; i++){
                        newQueue[i] = this.playerQueue[i];
                    }
                    newQueue[queueLength] = song;
                    this.playerQueue = newQueue;
                    this.playerWindow.updateQueueList(newQueue);
                }
                finally {
                    this.thread.unlock();
                }

            };
            try{
                this.thread.lock();
                this.addSongWindow = new AddSongWindow(createID(), addSongOk, this.playerWindow.getAddSongWindowListener());
                this.isPlaying = true;
            }
            finally {
                this.thread.unlock();
            }
        });
        t_add.start();

    }

    private void start() {
        Thread start_music = new Thread(() -> {
            try{
                this.thread.lock();
                SwingUtilities.invokeLater(() -> {
                    this.playerWindow.queuePanel.playNowButton.setEnabled(false);
                });
                if (this.sp_thread != null && !this.sp_thread.isInterrupted()){
                    this.sp_thread.interrupt();
                    this.sp_thread = null;
                }
                int song_id = playerWindow.getSelectedSongID();
                String[] song = {};
                int i = 0;
                for (String[] s: this.playerQueue){
                    if (Integer.parseInt(s[6]) == song_id){
                        song = s;
                        this.currentSongQueueId = i;
                        break;
                    }
                    i++;
                }
                this.currentSong = song;
                this.sp_thread = new SongPlayingThread(this.playerWindow, Integer.parseInt(song[5]), 0, this.currentSongQueueId,
                        this.playerQueue.length);
                playerWindow.enableScrubberArea();
                playerWindow.updatePlayingSongInfo(song[0], song[1], song[2]);
                this.sp_thread.start();
                this.isPlaying = true;
            }
            finally {
                this.thread.unlock();
            }
        });
        start_music.start();
    }

    private void remove() {
        Thread remove = new Thread(() -> {
            try {
                this.thread.lock();
                int queueLength = this.playerQueue.length;
                String[][] newQueue = new String[queueLength - 1][7];
                int song_id = this.playerWindow.getSelectedSongID();
                int old_index = 0;

                for (int i = 0; i < queueLength - 1; i++){
                    if (song_id == Integer.parseInt(this.playerQueue[old_index][6])){
                        old_index++;
                    }

                    newQueue[i] = this.playerQueue[old_index];
                    old_index++;

                }
                if (this.isPlaying) {
                    if (song_id == Integer.parseInt(this.currentSong[6])) {
                        this.sp_thread.interrupt();
                        this.playerWindow.disableScrubberArea();
                        this.playerWindow.updateMiniplayer(false, false, false,
                                0, 0, 0, this.playerQueue.length);
                    }
                }
                this.playerQueue = newQueue;
                this.playerWindow.updateQueueList(newQueue);
            }
            finally {
                this.thread.unlock();
            }
        });
        remove.start();


    }

}

