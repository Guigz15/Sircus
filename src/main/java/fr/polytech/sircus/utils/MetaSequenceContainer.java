package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MetaSequenceContainer implements Serializable
	{
	private final List< MetaSequence > metaSequences = new ArrayList<> ();

	public MetaSequenceContainer ()
		{
		Instant start = Instant.parse ( "2021-11-11T12:00:00.00Z" );
		Instant end   = Instant.parse ( "2021-11-11T15:00:00.00Z" );

		MetaSequence metaSequence1 = new MetaSequence ( "Metaséquence 1" );
		MetaSequence metaSequence2 = new MetaSequence ( "Metaséquence 2" );
		MetaSequence metaSequence3 = new MetaSequence ( "Metaséquence 3" );

		Sequence sequence1 = new Sequence ( "Sequence 1" );
		Sequence sequence2 = new Sequence ( "Sequence 2" );
		Sequence sequence3 = new Sequence ( "Sequence 3" );

		Media media1 = new Media ( "Media 1", "media/test.txt", Duration.between ( start, end ), TypeMedia.PICTURE, null);
		Media media2 = new Media ( "Media 2", "media/test.txt", Duration.between ( start, end ), TypeMedia.VIDEO, null );
		Media media3 = new Media ( "Media 3", "media/test.txt", Duration.between ( start, end ), TypeMedia.PICTURE, null );

		sequence1.addMedia ( media1 );
		sequence1.addMedia ( media2 );
		sequence2.addMedia ( media2 );
		sequence2.addMedia ( media3 );
		sequence3.addMedia ( media1 );
		sequence3.addMedia ( media3 );

		metaSequence1.addSequence ( sequence1 );
		metaSequence1.addSequence ( sequence2 );
		metaSequence2.addSequence ( sequence2 );
		metaSequence2.addSequence ( sequence3 );
		metaSequence3.addSequence ( sequence1 );
		metaSequence3.remSequence ( sequence3 );

		metaSequences.add ( metaSequence1 );
		metaSequences.add ( metaSequence2 );
		metaSequences.add ( metaSequence3 );
		}

	public List< MetaSequence > getMetaSequences ()
		{
		return metaSequences;
		}
	public void addMetaSequence (MetaSequence metaSequence)
		{
		metaSequences.add ( metaSequence );
		}
	}
