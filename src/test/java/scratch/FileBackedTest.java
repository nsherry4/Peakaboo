package scratch;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import scratch.encoders.CompoundEncoder;
import scratch.encoders.compressors.Compressors;
import scratch.encoders.serializers.Serializers;
import scratch.single.FileBacked;

public class FileBackedTest {

	@Test
	public void string() throws IOException {
		String s = "\n" + 
				"\n" + 
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent nec sapien eu nulla egestas egestas ut nec arcu. Fusce sollicitudin nisl orci, eu scelerisque orci facilisis et. Donec efficitur tellus porta erat volutpat suscipit. Suspendisse vel tellus ullamcorper, tempor erat vel, rhoncus nulla. Proin id tellus odio. Donec in nulla id arcu imperdiet tempus. Donec et augue risus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" + 
				"\n" + 
				"Donec vehicula pretium sapien, sed fermentum nibh scelerisque sed. Vestibulum elementum ante ante, eu elementum arcu rutrum non. Duis at lectus id nisl placerat rutrum. Nullam est ante, iaculis eu lacinia et, tincidunt sit amet velit. Vivamus sagittis condimentum finibus. Pellentesque aliquet tortor viverra, posuere ante in, ultrices metus. Donec commodo velit magna, quis luctus nunc commodo in. Nulla vestibulum quam vel nulla eleifend, at sagittis velit posuere. Sed facilisis, purus at pretium elementum, purus turpis pretium nisi, ut cursus tortor felis eu tortor. Interdum et malesuada fames ac ante ipsum primis in faucibus. Sed semper magna non venenatis dapibus. Donec nec sodales tortor.\n" + 
				"\n" + 
				"Curabitur placerat gravida tortor, vitae varius nibh volutpat a. Donec iaculis finibus sem, id scelerisque velit feugiat ac. Curabitur a risus pellentesque turpis pellentesque faucibus. Proin ut auctor tortor, nec tincidunt felis. Aenean pharetra mauris vel enim egestas, dictum condimentum libero cursus. Sed sit amet interdum enim. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vestibulum finibus metus arcu, quis convallis nisi congue in. In hac habitasse platea dictumst.\n" + 
				"\n" + 
				"Suspendisse a massa vulputate, lacinia lorem vel, ultrices est. In dolor enim, commodo a feugiat sit amet, placerat eu enim. Ut commodo vulputate neque vitae varius. Vestibulum sit amet odio placerat lorem luctus condimentum. Mauris lorem felis, scelerisque id semper vel, commodo a metus. Pellentesque commodo diam elit, vitae facilisis dui molestie ac. Maecenas sagittis a libero vitae placerat. Donec vel elit vel sem ullamcorper porttitor ac id neque. Fusce accumsan efficitur venenatis. Duis id semper eros. Duis fermentum justo quis convallis euismod. Integer at scelerisque sem. Sed faucibus, mi et vulputate rutrum, tortor sapien scelerisque lacus, a sagittis libero nisl id odio.\n" + 
				"\n" + 
				"Donec erat libero, sagittis ut magna eget, mattis pharetra est. Quisque vitae accumsan sapien. Vestibulum sed consequat nulla. Morbi molestie sapien arcu, in varius ligula iaculis a. Vestibulum ex urna, convallis at hendrerit sit amet, porttitor pharetra metus. Nunc eget rutrum mauris, a luctus mi. Ut euismod leo arcu, non laoreet ante ullamcorper sit amet. In hac habitasse platea dictumst. Integer luctus ex elit, sit amet vestibulum mauris dignissim vitae. Nulla facilisi. Suspendisse eu tempor eros.";
		
		FileBacked<String> fb = FileBacked.create(s,new CompoundEncoder<>(Serializers.java(), Compressors.lz4()));
		Assert.assertEquals(s, fb.get());
	}
	
}
