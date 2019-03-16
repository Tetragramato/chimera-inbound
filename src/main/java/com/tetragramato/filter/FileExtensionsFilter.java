package com.tetragramato.filter;

import org.springframework.integration.file.filters.FileListFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vivienbrissat
 * Date: 2019-01-11
 */
public class FileExtensionsFilter implements FileListFilter<File> {

    private List<String> extensions;

    public FileExtensionsFilter(final List<String> extensions) {
        this.extensions = extensions;
    }

    @Override
    public List<File> filterFiles(final File[] files) {
        return Arrays.stream(files).filter(e->{
            for (String ext : extensions) {
                if (e.getName().endsWith(ext)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
}
