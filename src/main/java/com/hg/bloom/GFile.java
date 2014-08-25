package com.hg.bloom;

import java.io.*;

/**
 * User: rexsheng
 * Date: 2007-8-2
 * Time: 17:32:35
 */
public class GFile {

    private static final String ENC = "UTF-8";
    private int size;

    private byte[] finger;
    private File file;

    public GFile(File file) throws Exception {
        this.file = file;
        long appr = file.length() / 8;
        size = (appr > Integer.MAX_VALUE / 2) ? Integer.MAX_VALUE / 2 : (int) appr;
        this.finger = new byte[size * 2];
        System.out.println("finger fingerSize set to: " + size * 2);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENC));
        for (String s; (s = in.readLine()) != null;) {
            if (s.trim().equals("")) continue;
            MersenneTwisterFast r = new MersenneTwisterFast(s);
            for (int i = 0; i < 8; i++) {
                long p = r.nextLong(size * 16l);
                int a = (int) p / 8;
                int b = 1 << (p % 8);
                finger[a] |= b;
            }
        }
        System.out.println("finger generated");
    }

    public GFile(byte[] finger) {
        this.finger = finger;
        this.size = finger.length / 2;
    }

    public void persist() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.file.getAbsoluteFile() + ".finger")));
        out.writeObject(finger);
        out.flush();
        out.close();
    }

    public void share(File f, Writer writer) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), ENC));
        for (String s; (s = in.readLine()) != null;)
            if (!isNew(s)) writer.append(s).append('\n');
    }

    public void substractFrom(File f, Writer writer) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), ENC));
        for (String s; (s = in.readLine()) != null;)
            if (isNew(s)) writer.append(s).append('\n');
    }

    public boolean isNew(String word) {
        if (word != null && word.length() >= 2) {
            MersenneTwisterFast r = new MersenneTwisterFast(word);
            for (int i = 0; i < 8; i++) {
                long p = r.nextLong(size * 16l);
                int a = (int) p / 8;
                int b = 1 << (p % 8);
                if ((finger[a] & b) != b) return true;
            }
        }
        return false;
    }

    public static void cut(File file, Writer to, int from) throws Exception {
        BufferedReader r = new BufferedReader(new FileReader(file));
        int i = 0;
        for (String s; (s = r.readLine()) != null;) {
            if (i++ < from) continue;
            to.append(s).append('\n');
        }
    }

    public static String find(File file, String word) throws Exception {
        BufferedReader r = new BufferedReader(new FileReader(file));
        for (String s; (s = r.readLine()) != null;) {
            if (s.indexOf(word) != -1) return s;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 1 && args[0].equals("sub")) {
            if (args.length < 3) {
                System.out.println("Error usage! To use sub:\nsub [file1] [file2]\n and output [file1].sub which is 'substract [file1] from [file2]'");
                System.exit(-1);
            }

            long t = System.currentTimeMillis();
            final GFile besub;
            if (args[1].endsWith(".finger")) {
                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(args[1])));
                byte[] temp = (byte[]) in.readObject();
                in.close();
                besub = new GFile(temp);
            } else
                besub = new GFile(new File(args[1]));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[2] + ".subfrom" + new File(args[1]).getName())), ENC));
            for (int i = 2; i < args.length; i++) {
                System.out.println("Substracting " + args[1] + " from " + args[i] + "...");
                besub.substractFrom(new File(args[i]), writer);
            }
            writer.flush();
            writer.close();
            System.out.println("Finished! (cost=" + (System.currentTimeMillis() - t) + "ms)");
            System.exit(0);
        }
        if (args.length >= 1 && args[0].equals("genkey")) {
            long t = System.currentTimeMillis();
            new GFile(new File(args[1])).persist();
            System.out.println("Finished! (cost=" + (System.currentTimeMillis() - t) + "ms)");
            System.exit(0);
        }
        if (args.length >= 1 && args[0].equals("share")) {
            if (args.length < 3) {
                System.out.println("Error usage! To use sub:\nsub [file1] [file2]\n and output [file1].sub which is 'substract [file1] from [file2]'");
                System.exit(-1);
            }

            long t = System.currentTimeMillis();
            final GFile besub = new GFile(new File(args[1]));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[1] + ".share")), ENC));
            for (int i = 2; i < args.length; i++) {
                System.out.println("Substracting " + args[1] + " from " + args[i] + "...");
                besub.substractFrom(new File(args[i]), writer);
            }
            writer.flush();
            writer.close();
            System.out.println("Finished! (cost=" + (System.currentTimeMillis() - t) + "ms)");
            System.exit(0);
        }

        if (args.length >= 1 && args[0].equals("cut")) {
            if (args.length < 3) {
                System.out.println("Error usage! To use cut:\ncut [inputfile] [fromline]");
                System.exit(-1);
            }
            long t = System.currentTimeMillis();
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[1] + ".cut")), ENC));
            cut(new File(args[1]), writer, new Integer(args[2]));
            writer.flush();
            writer.close();
            System.out.println("Finished! (cost=" + (System.currentTimeMillis() - t) + "ms)");
            System.exit(0);
        }
        if (args.length >= 1 && args[0].equals("find")) {
            if (args.length < 3) {
                System.out.println("Error usage! To use find:\ncut [file] [word]");
                System.exit(-1);
            }
            long t = System.currentTimeMillis();
            System.out.println(find(new File(args[1]), args[2]) + "\nFinished! (cost=" + (System.currentTimeMillis() - t) + "ms)");
            System.exit(0);
        }

    }
}
