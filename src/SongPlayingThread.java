import ui.PlayerWindow;
public class SongPlayingThread extends Thread {
    private Player player;
    private PlayerWindow playerWindow;
    private int songLength;
    private int startSong;
    private int songQueueId;
    private int queueSize;

    public SongPlayingThread(Player player, PlayerWindow playerWindow, int songLength, int startSong, int songQueueId, int queueSize){
        this.player = player;
        this.playerWindow = playerWindow;
        this.songLength = songLength;
        this.startSong = startSong;
        this.songQueueId = songQueueId;
        this.queueSize = queueSize;
    }
    @Override
    public void run() {
        for (int i = startSong; i <= songLength; i++){
            try {
                this.playerWindow.updateMiniplayer(true, true, false, i,
                        this.songLength, this.songQueueId, this.queueSize);
                // System.out.println("thread started with " + this.startSong);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // System.out.println("thread interrupted");
                this.playerWindow.updateMiniplayer(true, false, false,
                        i,this.songLength, this.songQueueId, this.queueSize);
                return;
            }

        }
        if (player.rep_type == 0 && player.currentSongQueueId != player.playerQueue.length - 1){
            player.next();
        }
        else if (player.rep_type == 1) {
            this.player.changeSong(this.player.shuffleQueue[this.player.shuffleIndex++]);
        }
        else if (player.rep_type == 2){
            player.start();
        }

    }

}
