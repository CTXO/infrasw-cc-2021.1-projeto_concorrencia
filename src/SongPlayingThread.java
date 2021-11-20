import ui.PlayerWindow;

public class SongPlayingThread extends Thread {
    private PlayerWindow playerWindow;
    private int songLength;
    private int startSong;
    private int songQueueId;
    private int queueSize;

    public SongPlayingThread(PlayerWindow playerWindow, int songLength, int startSong, int songQueueId, int queueSize){
        this.playerWindow = playerWindow;
        this.songLength = songLength;
        this.startSong = startSong;
        this.songQueueId = songQueueId;
        this.queueSize = queueSize;
    }
    @Override
    public void run() {
        for (int i = startSong; i < songLength; i++){
            try {
                this.playerWindow.updateMiniplayer(true, true, false, i,
                        this.songLength, this.songQueueId, this.queueSize);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                this.playerWindow.updateMiniplayer(true, false, false,
                        i,this.songLength, this.songQueueId, this.queueSize);
                return;
            }

        }
    }
}
