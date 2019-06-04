package com.tetragramato.filter;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author vivienbrissat
 * Date: 2019-03-18
 */
public class FileExtensionsFilterTest {

    @Test
    public void testFileExtensionsFilter(){
        //Given
        List<String> listExtensions = Arrays.asList(".txt",".pdf");

        File[] files = new File[] {new File("toto.pdf"), new File("titi.txt"), new File("tata.xml")};

        FileExtensionsFilter fileExtensionsFilter = new FileExtensionsFilter(listExtensions);

        //When
        List<File> filesFiltered = fileExtensionsFilter.filterFiles(files);

        //Then
        assertThat(filesFiltered.size()).isEqualTo(2);
        assertThat(filesFiltered).asList().isSubsetOf((Object[])files);
    }

}