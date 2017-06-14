import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.stream.Stream;

class MyCleaner<P extends Path> implements FileVisitor<P> {
    private Path source;
    private Path destination;

    public MyCleaner(Path source, Path destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public FileVisitResult preVisitDirectory(P dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(P file, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(P file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(P dir, IOException exc) throws IOException {
        checkAndDelete(dir);
        return FileVisitResult.CONTINUE;
    }

    private void checkAndDelete(Path dir) throws IOException {
        Stream<Path> destPathContent = Files.list(dir);
        Iterator<Path> iterator = destPathContent.iterator();
        while (iterator.hasNext()) {
            Path path = iterator.next();
            Path relativePath = destination.relativize(path);
            Path currSourcePath = source.resolve(relativePath);
            if (Files.notExists(currSourcePath)) {
                try {
                    Files.delete(path);
                    System.out.println("\tdeleted " + path);
                } catch (DirectoryNotEmptyException e){
                    System.out.println("\tDirectory " + path + " is not empty");
                }
            }
        }
    }

}
