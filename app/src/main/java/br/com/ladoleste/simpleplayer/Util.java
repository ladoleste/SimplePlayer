package br.com.ladoleste.simpleplayer;

import java.io.File;

/**
 * Created by Anderson Silva on 22/12/2016.
 */

final class Util {
    private Util() {
    }

    public static String getExtension(File file) {
        return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
    }
}
