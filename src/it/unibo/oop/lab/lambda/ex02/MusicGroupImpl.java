package it.unibo.oop.lab.lambda.ex02;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.oop.lab.lambda.ex02.MusicGroupImpl.Song;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
    	Set<String> str = new HashSet<String>();
    	songs.forEach(i -> str.add(i.getSongName()));
        return str.stream().sorted();
    }

    @Override
    public Stream<String> albumNames() {
    	Set<String> str = albums.keySet();
        return str.stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
    	
        return albums.entrySet().stream().filter(i -> i.getValue() == year).map(i -> i.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
    	Stream<Song> str = songs.stream().filter(i->i.getAlbumName().isPresent()).filter(i->i.getAlbumName().get().equals(albumName));		
        return (int) str.count();
    }

    @Override
    public int countSongsInNoAlbum() {
    	Stream<Song> str = songs.stream().filter(i->i.getAlbumName().isEmpty());		
        return (int) str.count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
    	Stream<Song> str = songs.stream().filter(i->i.getAlbumName().isPresent()).filter(i->i.getAlbumName().get().equals(albumName));
        return str.mapToDouble(s -> s.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
    	Optional<Song> lS =songs.stream().max((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration()));
        return Optional.ofNullable(lS.get().getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
    	Stream<Song> strSongInAlbums = songs.stream().filter(i->i.getAlbumName().isPresent());
    	Map<Optional<String>, Double> hm = strSongInAlbums.collect(Collectors.groupingBy(Song::getAlbumName, Collectors.summingDouble(Song::getDuration)));
        return hm.keySet().stream().max((s1, s2) -> Double.compare(hm.get(s1), hm.get(s2))).get();
    }

    public static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
