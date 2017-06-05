import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class FileSynch {
    public static void main(String[] args) {
        String source = "/home/maistrenko/Java/FileSynch";
        Path pathIn  = Paths.get(source,"");
        String destination = "/home/maistrenko/Java/FileSynch/dest";
        Path pathOut  = Paths.get(destination);
        try {
            Stream s = Files.list(pathIn);

            Files.walkFileTree(pathIn, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println("dir: " + dir + " folder : " + dir.getFileName());
                    String currTargetPath = dir.toString().replaceFirst(source,destination);
                    try {
                        Files.copy(dir,Paths.get(currTargetPath));

                    } catch (IOException e) {
                        System.out.println(" file " + currTargetPath + " already exist ");
                    }
                    findFile(dir,dir.getFileName().toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println(file.toRealPath());
                    findFile(file,file.getFileName().toString());
                    String currTargetPath = file.toString().replaceFirst(pathIn.toString(), pathOut.toString());
                    try {
                        Files.copy(file, Paths.get(currTargetPath));
                    } catch (IOException e) {
                        System.out.println(" file " + currTargetPath + " already exist ");
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });

//            Iterator<Path> iterator = s.iterator();
//            while (iterator.hasNext()) {
//                Path file = iterator.next();
//
//                System.out.println(file.toAbsolutePath() + " is direcory : " + Files.isDirectory(file));
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static void findFile(Path path, String fileToFind){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileString = file.toAbsolutePath().toString();
                    //System.out.println("pathString = " + fileString);

                    if(fileString.endsWith(fileToFind)){
                        System.out.println("file found at path: " + file.toAbsolutePath());
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
