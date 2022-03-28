
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.FileSystem;

public class Main {
    static Context context;

    public static void main(String[] args) {

        init();

        try {
            test1();
        } catch (Exception e) {
        }

        try {
            test2();
        } catch (Exception e) {
        }

        try {
            test3();
        } catch (Exception e) {
        }
    }

    public static void init() {
        Path engineWorkingDirectory = Paths.get(System.getProperty("user.dir"));

        Context.Builder contextBuilder = Context.newBuilder("js")
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL);

        FileSystem fs = FileSystem.newDefaultFileSystem();
        fs.setCurrentWorkingDirectory(engineWorkingDirectory);
        contextBuilder.fileSystem(fs);
        try {
            Path path = Paths.get(engineWorkingDirectory + "/node_modules");

            ///// COMMENT THESE TWO LINES /////
            contextBuilder.option("js.commonjs-require", "true");
            contextBuilder.option("js.commonjs-require-cwd", path.toString());
            ///////////////////////////////////

        } catch (InvalidPathException e) {
        }

        context = contextBuilder.build();
    }

    public static void test1() {
        test("js/main.mjs");
    }

    public static void test2() {
        test("js/test2.mjs");
    }

    public static void test3() {
        test("js/test3.mjs");
    }

    public static void test(String filepath) {
        File jsFile = new File(filepath);

        try {
            context.eval(Source.newBuilder("js", jsFile).build());
        } catch (IOException e) {
            throw new RuntimeException("Could not load main file.", e);
        } catch (final PolyglotException e) {
            e.printStackTrace();
            // throw new RuntimeException("Exception when evaluating javascript", e);
        }
    }

    public static void fromJsAndBack() {

        var graalSource = Source.newBuilder("js", new StringBuilder(
                "import { _hasClavaData } from 'node_modules/clavaapi/dist/clava/js/DataHandler.js'; _hasClavaData;"),
                "[JAVA1]");
        try {
            // return eval(Source.newBuilder("js", new StringBuilder(code), source)
            context.eval(graalSource
                    .mimeType("application/javascript+module")
                    .build());
        } catch (IOException e) {
            throw new RuntimeException("Could not load JS code as module", e);
        }

    }

    public static void fromJsAndBack2() {

        var graalSource = Source.newBuilder("js", new StringBuilder(
                "import { _hasClavaData } from 'clavaapi/dist/clava/js/DataHandler.js'; _hasClavaData;"),
                "[JAVA2]");
        try {
            // return eval(Source.newBuilder("js", new StringBuilder(code), source)
            context.eval(graalSource
                    .mimeType("application/javascript+module")
                    .build());
        } catch (IOException e) {
            throw new RuntimeException("Could not load JS code as module", e);
        }

    }

}
