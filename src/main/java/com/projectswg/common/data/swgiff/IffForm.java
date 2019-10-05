package com.projectswg.common.data.swgiff;

import me.joshlarson.jlcommon.utilities.Arguments;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class IffForm extends IffNode {
	
	private final String tag;
	private final int version;
	private final List<IffNode> children;
	
	private boolean read;
	
	private IffForm(String tag, int version, List<IffNode> children) {
		this.tag = tag;
		this.version = version;
		this.children = children;
		this.read = false;
	}
	
	@Override
	public void close() {
		read = true;
	}
	
	@Override
	public String getTag() {
		return tag;
	}
	
	public int getVersion() {
		return version;
	}
	
	@Override
	public boolean isForm() {
		return true;
	}
	
	@Override
	public boolean isRead() {
		return read;
	}
	
	public List<IffNode> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public boolean hasChunk() {
		for (IffNode child : children) {
			if (!child.isForm() && !child.isRead()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasChunk(String tag) {
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && !child.isForm() && !child.isRead()) {
				return true;
			}
		}
		return false;
	}
	
	public IffChunk readChunk() {
		for (IffNode child : children) {
			if (!child.isForm() && !child.isRead()) {
				return (IffChunk) child;
			}
		}
		return null;
	}
	
	public IffChunk readChunk(String tag) {
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && !child.isForm() && !child.isRead()) {
				return (IffChunk) child;
			}
		}
		return null;
	}
	
	public boolean readChunkIfPresent(String tag, Consumer<IffChunk> processor) {
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && !child.isForm() && !child.isRead()) {
				processor.accept((IffChunk) child);
				return true;
			}
		}
		return false;
	}
	
	public int readAllChunks(String tag, Consumer<IffChunk> processor) {
		int count = 0;
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && !child.isForm() && !child.isRead()) {
				processor.accept((IffChunk) child);
				count++;
			}
		}
		return count;
	}
	
	public boolean hasForm() {
		for (IffNode child : children) {
			if (child.isForm() && !child.isRead()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasForm(String tag) {
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && child.isForm() && !child.isRead()) {
				return true;
			}
		}
		return false;
	}
	
	public IffForm readForm() {
		for (IffNode child : children) {
			if (child.isForm() && !child.isRead()) {
				return (IffForm) child;
			}
		}
		return null;
	}
	
	public IffForm readForm(String tag) {
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && child.isForm() && !child.isRead()) {
				return (IffForm) child;
			}
		}
		return null;
	}
	
	public boolean readFormIfPresent(String tag, Consumer<IffForm> processor) {
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && child.isForm() && !child.isRead()) {
				processor.accept((IffForm) child);
				return true;
			}
		}
		return false;
	}
	
	public int readAllForms(String tag, Consumer<IffForm> processor) {
		int count = 0;
		for (IffNode child : children) {
			if (child.getTag().equals(tag) && child.isForm() && !child.isRead()) {
				processor.accept((IffForm) child);
				count++;
			}
		}
		return count;
	}
	
	public void printTree() {
		printTree(this, 0);
	}
	
	@Override
	public String toString() {
		return "IffForm["+tag+"]";
	}
	
	private void printTree(IffNode node, int depth) {
		for (int i = 0; i < depth; i++)
			System.out.print("\t");
		if (node.isForm()) {
			System.out.println(node.getTag() + "[version=" + ((IffForm) node).getVersion() + "]");
			for (IffNode child : ((IffForm) node).getChildren()) {
				printTree(child, depth + 1);
			}
		} else {
			System.out.println(node.getTag());
		}
	}
	
	public static IffForm of(String tag, IffNode ... nodes) {
		return new IffForm(tag, -1, List.of(nodes));
	}
	
	public static IffForm of(String tag, int version, IffNode ... nodes) {
		Arguments.validate(version >= 0, "Version must be greater than or equal to zero");
		return new IffForm(tag, version, List.of(nodes));
	}
	
	public static IffForm of(String tag, Collection<? extends IffNode> nodes) {
		return new IffForm(tag, -1, new ArrayList<>(nodes));
	}
	
	public static IffForm of(String tag, int version, Collection<? extends IffNode> nodes) {
		Arguments.validate(version >= 0, "Version must be greater than or equal to zero");
		return new IffForm(tag, version, new ArrayList<>(nodes));
	}
	
	public static IffForm read(File file) throws IOException {
		try (FileChannel channel = FileChannel.open(file.toPath())) {
			MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0, channel.size());
			int size = (int) channel.size();
			Arguments.validate(isValidIff(bb, size), "invalid iff");
			
			return read(bb);
		}
	}
	
	public static IffForm read(ByteBuffer data) {
		data.order(ByteOrder.BIG_ENDIAN);
		// Initial type
		String tag = readTag(data);
		assert tag.equals("FORM") : "invalid IFF";
		int length = data.getInt();
		tag = readTag(data);
		int version = -1;
		
		List<IffNode> children = new ArrayList<>();
		int endPosition = data.position() + length - 4;
		while (data.position() < endPosition) {
			String childTag = readTag(data);
			if (childTag.equals("FORM")) {
				data.position(data.position()-4);
				IffForm form = read(data);
				if (form.isVersionForm() && version == -1) {
					version = form.calculateVersionFromTag();
					children.addAll(form.getChildren());
				} else {
					children.add(form);
				}
			} else {
				int childLength = data.getInt();
				byte [] childData = new byte[childLength];
				data.get(childData);
				children.add(new IffChunk(childTag, childData));
			}
		}
		return new IffForm(tag, version, children);
	}
	
	private static boolean isValidIff(ByteBuffer buffer, int size) {
		buffer.mark();
		
		if (buffer.remaining() < 8 || buffer.get() != 'F' || buffer.get() != 'O' || buffer.get() != 'R' || buffer.get() != 'M')
			return false;
		
		int formSize = buffer.getInt();
		if (size != (formSize) + 8)
			return false;
		
		buffer.reset();
		return true;
	}
	
	private static String readTag(ByteBuffer data) {
		return "" + (char) data.get() + (char) data.get() + (char) data.get() + (char) data.get();
	}
	
}
