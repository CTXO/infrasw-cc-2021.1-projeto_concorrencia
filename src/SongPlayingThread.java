import ui.PlayerWindow;

public class SongPlayingThread extends Thread {
    private PlayerWindow playerWindow;
    private int songLength;
    private int startSong;
    private int song_id;
    private int queueSize;

    public SongPlayingThread(PlayerWindow playerWindow, int songLength, int startSong, int song_id, int queueSize){
        this.playerWindow = playerWindow;
        this.songLength = songLength;
        this.startSong = startSong;
        this.song_id = song_id;
        this.queueSize = queueSize;
    }
    @Override
    public void run() {
        for (int i = startSong; i < songLength; i++){
            try {
                Thread.sleep(1000);
                this.playerWindow.updateMiniplayer(true, true, false, i,
                        this.songLength, this.song_id, this.queueSize);
            } catch (InterruptedException e) {
                this.playerWindow.updateMiniplayer(true, false, false, i - 1,this.songLength, this.song_id, this.queueSize);
                return;
            }

        }
    }
}
