# jfile-generator

## OverView

jfile-generator is a simple generator that generates Java files from JSON and XML String.

for example,

```XML
{
  "artist": {
    "name": "Radiohead",
    "genre": "Alternative",
    "albums": {
      "album": {
        "name": "The Bends",
        "release": "1995/3/13",
        "songs": {
          "song": [
            "Planet Telex",
            "The Bends",
            "High and Dry",
            "Fake Plastic Trees",
            "Bones",
            "Nice Dream",
            "Just",
            "My Iron Lung",
            "Bullet Proof...I Wish I Was",
            "Black Star",
            "Sulk",
            "Stree Spirit(Fade Out)"
          ]
        }
      }
    }
  }
}
```

to

```java
import java.util.List;

public class Artist {
	private String name;

	private String genre;

	private List<Album> albums;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGenre() {
		return this.genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public List<Album> getAlbums() {
		return this.albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}
}

class Album {
	private String name;

	private Date release;

	private List<String> songs;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getRelease() {
		return this.release;
	}

	public void setRelease(Date release) {
		this.release = release;
	}

	public List<String> getSongs() {
		return this.songs;
	}

	public void setSongs(List<String> songs) {
		this.songs = songs;
	}
}
```



## How to use

TODO
