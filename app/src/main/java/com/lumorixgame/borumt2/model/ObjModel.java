package com.lumorixgame.borumt2.model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjModel {

    public final FloatBuffer vertices;
    public final FloatBuffer texCoords;
    public final FloatBuffer normals;
    public final int vertexCount;

    private ObjModel(
            FloatBuffer vertices,
            FloatBuffer texCoords,
            FloatBuffer normals,
            int vertexCount
    ) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.vertexCount = vertexCount;
    }

    public static ObjModel load(Context context, String assetPath) throws Exception {
        List<float[]> positions = new ArrayList<>();
        List<float[]> tex = new ArrayList<>();
        List<float[]> norm = new ArrayList<>();

        List<Float> outVertices = new ArrayList<>();
        List<Float> outTexCoords = new ArrayList<>();
        List<Float> outNormals = new ArrayList<>();

        InputStream input = context.getAssets().open(assetPath);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(input)
        );

        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("v ")) {
                String[] p = line.substring(2).trim().split("\\s+");

                positions.add(new float[]{
                        Float.parseFloat(p[0]),
                        Float.parseFloat(p[1]),
                        Float.parseFloat(p[2])
                });

            } else if (line.startsWith("vt ")) {
                String[] p = line.substring(3).trim().split("\\s+");

                tex.add(new float[]{
                        Float.parseFloat(p[0]),
                        1.0f - Float.parseFloat(p[1])
                });

            } else if (line.startsWith("vn ")) {
                String[] p = line.substring(3).trim().split("\\s+");

                norm.add(new float[]{
                        Float.parseFloat(p[0]),
                        Float.parseFloat(p[1]),
                        Float.parseFloat(p[2])
                });

            } else if (line.startsWith("f ")) {
                String[] face = line.substring(2).trim().split("\\s+");

                if (face.length < 3) {
                    continue;
                }

                // Üçgen ve dörtgen yüzleri destekle.
                addVertex(face[0], positions, tex, norm,
                        outVertices, outTexCoords, outNormals);

                addVertex(face[1], positions, tex, norm,
                        outVertices, outTexCoords, outNormals);

                addVertex(face[2], positions, tex, norm,
                        outVertices, outTexCoords, outNormals);

                if (face.length == 4) {
                    addVertex(face[0], positions, tex, norm,
                            outVertices, outTexCoords, outNormals);

                    addVertex(face[2], positions, tex, norm,
                            outVertices, outTexCoords, outNormals);

                    addVertex(face[3], positions, tex, norm,
                            outVertices, outTexCoords, outNormals);
                }
            }
        }

        reader.close();

        FloatBuffer vertexBuffer = createBuffer(outVertices);
        FloatBuffer texBuffer = createBuffer(outTexCoords);
        FloatBuffer normalBuffer = createBuffer(outNormals);

        return new ObjModel(
                vertexBuffer,
                texBuffer,
                normalBuffer,
                outVertices.size() / 3
        );
    }

    private static void addVertex(
            String index,
            List<float[]> positions,
            List<float[]> tex,
            List<float[]> norm,
            List<Float> outVertices,
            List<Float> outTexCoords,
            List<Float> outNormals
    ) {
        String[] parts = index.split("/");

        int positionIndex = parseIndex(parts, 0);
        int texIndex = parseIndex(parts, 1);
        int normalIndex = parseIndex(parts, 2);

        float[] position = getSafe(positions, positionIndex);
        float[] uv = getSafe(tex, texIndex);
        float[] normal = getSafe(norm, normalIndex);

        outVertices.add(position[0]);
        outVertices.add(position[1]);
        outVertices.add(position[2]);

        outTexCoords.add(uv[0]);
        outTexCoords.add(uv[1]);

        outNormals.add(normal[0]);
        outNormals.add(normal[1]);
        outNormals.add(normal[2]);
    }

    private static int parseIndex(String[] parts, int index) {
        if (index >= parts.length || parts[index].isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(parts[index]) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static float[] getSafe(List<float[]> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }

        if (list == null || list.isEmpty()) {
            return new float[]{0f, 0f, 0f};
        }

        return list.get(0);
    }

    private static FloatBuffer createBuffer(List<Float> values) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(
                values.size() * 4
        );

        buffer.order(ByteOrder.nativeOrder());

        FloatBuffer floatBuffer = buffer.asFloatBuffer();

        for (Float value : values) {
            floatBuffer.put(value);
        }

        floatBuffer.position(0);

        return floatBuffer;
    }
}
