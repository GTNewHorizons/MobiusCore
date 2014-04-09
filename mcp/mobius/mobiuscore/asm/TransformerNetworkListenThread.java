package mcp.mobius.mobiuscore.asm;
import java.util.ArrayList;
import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformerNetworkListenThread extends TransformerBase {

	private static String NETWORKTICK;
	
	private static AbstractInsnNode[] NETWORKTICK_PAYLOAD_TOP;
	private static AbstractInsnNode[] NETWORKTICK_PAYLOAD_BOTTOM;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		NETWORKTICK = "b ()V";
		
		NETWORKTICK_PAYLOAD_TOP = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.NETWORK_TICK.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "()V")};				
		
		NETWORKTICK_PAYLOAD_BOTTOM = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.NETWORK_TICK.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "()V")};			
		
	}	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(NETWORKTICK)){
        		System.out.printf("Found NetworkListenThread.networkTick()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, NETWORKTICK_PAYLOAD_TOP);
        		this.applyPayloadLast(instructions, NETWORKTICK_PAYLOAD_BOTTOM);        		
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}
