package me.hugmanrique.slime.core.tests;

import com.google.common.collect.ImmutableSet;
import me.hugmanrique.slime.core.data.ProtoSlimeChunk;
import me.hugmanrique.slime.core.data.SlimeFile;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SlimeFileTests {

    private static File SLIME_TEST_FILE = new File("src/test/resources/skyblock.slime");

    @BeforeAll
    static void initAll() {
        // Recalculating counts causes an "Accessed blocks before bootstrap" exception
        ProtoSlimeChunk.RECALC_BLOCK_COUNTS = false;
    }

    @Test
    void testFileRead() throws IOException {
        SlimeFile file = SlimeFile.read(SLIME_TEST_FILE);

        assertEquals(1, file.getVersion());

        assertEquals((short) 0xFFFB, file.getMinX(), "Lowest chunk X should be -5");
        assertEquals((short) 0xFFFF, file.getMinZ(), "Lowest chunk Z should be -1");
        assertEquals(6, file.getWidth());
        assertEquals(2, file.getDepth());

        BitSet populated = file.getPopulatedChunks();

        assertEquals(2, populated.toByteArray().length, "BitSet should have appropriate length");
        Set<Integer> shouldBePopulated = ImmutableSet.of(0, 4, 5, 6, 10, 11);

        // Check BitSet entries
        for (int i = 0; i < 16; i++) {
            boolean expected = shouldBePopulated.contains(i);

            assertEquals(expected, populated.get(i), "Chunk " + i + " populated data should be " + expected);
        }

        assertTrue(file.getEntities().isEmpty(), "Version 1 Slime file should have no entity data");

        // Check lava and water buckets chest
        NBTTagCompound initialChest = file.getTileEntities().get(0);

        assertEquals("Chest", initialChest.getString("id"));
        assertEquals(4, initialChest.getInt("x"));
        assertEquals(67, initialChest.getInt("y"));
        assertEquals(-3, initialChest.getInt("z"));
        assertNotNull(initialChest.getList("Items", 10));

        // TODO Check some blocks

    }
}
