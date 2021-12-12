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
                if (player.shuffle_active) {
                    this.playerWindow.updateMiniplayer(true, true, player.repeat_active, i,
                            this.songLength, this.player.shuffleIndex, this.queueSize);
                    // System.out.println("thread started with " + this.startSong);
                    Thread.sleep(1000);
                }
                else {
                    this.playerWindow.updateMiniplayer(true, true, player.repeat_active, i,
                            this.songLength, this.songQueueId, this.queueSize);
                    // System.out.println("thread started with " + this.startSong);
                    Thread.sleep(1000);
                }

                System.out.println("Shuffle index: " + player.shuffleIndex);
            } catch (InterruptedException e) {

                // System.out.println("thread interrupted");
                if (this.player.shuffle_active){
                    this.playerWindow.updateMiniplayer(true, false, player.repeat_active,
                            i,this.songLength, this.player.shuffleIndex, this.queueSize);
                    return;
                }
                else {
                    this.playerWindow.updateMiniplayer(true, false, player.repeat_active,
                            i, this.songLength, this.songQueueId, this.queueSize);
                    return;
                }
            }
        }

        if (player.shuffle_active && player.repeat_active){
            player.changeSong(player.shuffleQueue[++player.shuffleIndex % player.shuffleQueue.length]);
        }
        else if (player.shuffle_active) {
            if (player.shuffleIndex != player.shuffleQueue.length - 1) {
                player.changeSong(player.shuffleQueue[++player.shuffleIndex]);
            }
            else {
                player.stop();
            }
        }
        else if (player.repeat_active){
            if (player.currentSongQueueId != player.playerQueue.length - 1) {
                player.next();
            }
            else{
                player.changeSong(0);
            }
        }
        else {
            if (player.currentSongQueueId != player.playerQueue.length - 1) {
                player.next();
            }
            else{
                player.stop();
            }

        }
//        if (player.rep_type == 0){
//            if (player.currentSongQueueId != player.playerQueue.length - 1) {
//                player.next();
//            }
//            else {
//                player.stop();
//            }
//        }
//        else if (player.rep_type == 1) {
//            player.changeSong(player.shuffleQueue[player.shuffleIndex++ % player.shuffleQueue.length]);
//        }
//        else if (player.rep_type == 2){
//            if (player.currentSongQueueId != player.playerQueue.length - 1) {
//                player.next();
//            }
//            else{
//                player.changeSong(0);
//            }
//        }
    }

}
