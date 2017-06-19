package com.boredream.musicplayer.bean;

import android.support.annotation.NonNull;

import com.boredream.musicplayer.player.PlayMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:53 PM
 * Desc: PlayList
 */
public class PlayList<T extends Song> implements Serializable {

    public static final int NO_POSITION = -1;

    private String name;

    private int numOfSongs;

    private List<T> songs = new ArrayList<>();

    private int playingIndex = -1;

    /**
     * Use a singleton play mode
     */
    private PlayMode playMode = PlayMode.LIST;

    public PlayList() {
        // EMPTY
    }

    public PlayList(T song) {
        songs.add(song);
        numOfSongs = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    @NonNull
    public List<T> getSongs() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        return songs;
    }

    public void setSongs(List<T> songs) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        this.songs = songs;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    // Utils

    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public void addSong(T song) {
        if (song == null) return;

        songs.add(song);
        numOfSongs = songs.size();
    }

    public void addSong(T song, int index) {
        if (song == null) return;

        songs.add(index, song);
        numOfSongs = songs.size();
    }

    public void addSong(List<T> songs, int index) {
        if (songs == null || songs.isEmpty()) return;

        this.songs.addAll(index, songs);
        this.numOfSongs = this.songs.size();
    }

    public boolean removeSong(T song) {
        if (song == null) return false;

        int index;
        if ((index = songs.indexOf(song)) != -1) {
            if (songs.remove(index) != null) {
                numOfSongs = songs.size();
                return true;
            }
        } else {
            for (Iterator<T> iterator = songs.iterator(); iterator.hasNext(); ) {
                T item = iterator.next();
                if (song.getSongUrl().equals(item.getSongUrl())) {
                    iterator.remove();
                    numOfSongs = songs.size();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prepare to play
     */
    public boolean prepare() {
        if (songs.isEmpty()) return false;
        if (playingIndex == NO_POSITION) {
            playingIndex = 0;
        }
        return true;
    }

    /**
     * The current song being played or is playing based on the {@link #playingIndex}
     */
    public T getCurrentSong() {
        if (playingIndex != NO_POSITION) {
            return songs.get(playingIndex);
        }
        return null;
    }

    public boolean hasLast() {
        return songs != null && songs.size() != 0 && playingIndex > 0;
    }

    public T last() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if (newIndex < 0) {
                    newIndex = songs.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    /**
     * @return Whether has next song to play.
     * <p/>
     * If this query satisfies these conditions
     * - comes from media player's complete listener
     * - current play mode is PlayMode.LIST (the only limited play mode)
     * - current song is already in the end of the list
     * then there shouldn't be a next song to play, for this condition, it returns false.
     * <p/>
     * If this query is from user's action, such as from play controls, there should always
     * has a next song to play, for this condition, it returns true.
     */
    public boolean hasNext(boolean fromComplete) {
        if (songs.isEmpty()) return false;
        if (fromComplete) {
            if (playMode == PlayMode.LIST && playingIndex + 1 >= songs.size()) return false;
        }
        return true;
    }

    /**
     * Move the playingIndex forward depends on the play mode
     *
     * @return The next song to play
     */
    public T next() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if (newIndex >= songs.size()) {
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(songs.size());
        // Make sure not play the same song twice if there are at least 2 songs
        if (songs.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }

    public boolean isLastSong() {
        return playingIndex >= (numOfSongs - 1);
    }
}
