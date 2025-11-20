package com.bocchipet.models.activities;

import com.bocchipet.models.Player;

public interface IActivity {

    /**
     * Method buat ngejalanin logika yang bakal modif si player.
     * @param player Objek Player yang bakal dimodifikasi
     */
    void perform(Player player);

    /**
     * Ngedapeting kunci (nama) buat gambar animasi yang nanti muncul di UI
     * @return String nama resource gambar
     */
    String getAnimationImageKey();

    /**
     *Ngedapetin kunci (nama) buat suara efek yang nanti diputer
     * @return String nama resource suara
     */
    String getSoundEffectKey();
}