#usr/local/bin/perl
    opendir (DIR, ".");
    fileloop: while ( $d = readdir(DIR)) {
        print "file $d\n";
        if ( $d =~ m/\.ps/ ) {
            system "ps2pdf -dEmbedAllFonts $d";
        }

    }


