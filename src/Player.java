import ui.AddSongWindow;
import ui.PlayerWindow;

import java.awt.event.*;
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
        String[] selectedSong = this.playerQueue[idQueue];
        this.currentSongQueueId = idQueue;
        this.sp_thread.interrupt();
        this.sp_thread = new SongPlayingThread(this.playerWindow, Integer.parseInt(selectedSong[5]),
                0, this.currentSongQueueId, this.playerQueue.length);
        playerWindow.updatePlayingSongInfo(selectedSong[0], selectedSong[1], selectedSong[2]);
        this.currentSong = selectedSong;
        this.playerWindow.queuePanel.queueList.setRowSelectionInterval(this.currentSongQueueId, this.currentSongQueueId);
        this.sp_thread.start();
    }

    private void previous() {
       changeSong(this.currentSongQueueId - 1);

    }



    private void next() {
        changeSong(this.currentSongQueueId + 1);
    }



    private void stop() {
    }
    private void click_up() {
       // System.out.println("mouseup");
        this.sp_thread = new SongPlayingThread(this.playerWindow, Integer.parseInt(this.currentSong[5]), this.new_time
                ,this.currentSongQueueId, this.playerQueue.length);
        System.out.println(this.playerWindow.getScrubberValue());
        this.sp_thread.start();

    }
    private void click_down() {
        //System.out.println("mouse down");
        if (this.sp_thread != null && !this.sp_thread.isInterrupted()){
            this.sp_thread.interrupt();
            this.sp_thread = null;

            this.new_time = this.playerWindow.getScrubberValue();
            System.out.println("should update to " + this.new_time);
            drag();
            this.playerWindow.updateMiniplayer(true,true,false,this.new_time,
                    Integer.parseInt(this.currentSong[5]), this.currentSongQueueId, this.playerQueue.length);
        }


    }

    private void drag() {
        int value = this.playerWindow.getScrubberValue();
        // System.out.println("Dragged " + value);
        this.playerWindow.updateMiniplayer(true,true,false,value,
                Integer.parseInt(this.currentSong[5]), this.currentSongQueueId, this.playerQueue.length);
        this.new_time = playerWindow.getScrubberValue();
    }

    private void playPause() {
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

    private void add() {

        ActionListener addSongOk = e-> {
            int queueLength = this.playerQueue != null ? this.playerQueue.length : 0;
            String[] song = this.addSongWindow.getSong();
            String[][] newQueue = new String[queueLength + 1][7];
            for (int i = 0; i < queueLength; i++){
                newQueue[i] = this.playerQueue[i];
            }
            newQueue[queueLength] = song;
            this.playerQueue = newQueue;
            this.playerWindow.updateQueueList(newQueue);
        };
        this.addSongWindow = new AddSongWindow(createID(), addSongOk, this.playerWindow.getAddSongWindowListener());
        this.isPlaying = true;
    }

    private void start() {
        this.playerWindow.queuePanel.playNowButton.setEnabled(false);
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

    private void remove() {
        int queueLength = this.playerQueue.length;
        System.out.println("length");
        String[][] newQueue = new String[queueLength - 1][7];
        int song_id = playerWindow.getSelectedSongID();
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
                playerWindow.disableScrubberArea();
                playerWindow.updateMiniplayer(false, false, false,
                        0, 0, 0, this.playerQueue.length);
            }
        }
        this.playerQueue = newQueue;
        this.playerWindow.updateQueueList(newQueue);
    }

}

