# ZFL + iOS 26 Icon Pack (bawaan)

Modifikasi di atas ZFL Launcher: **icon pack iOS 26 sudah tertanam dan langsung aktif** setelah APK dipasang. Tanpa pengaturan, tanpa aplikasi tambahan, tanpa internet.

## Yang berubah
- `app/src/main/res/drawable-nodpi/ios26_*.png`: 85 aset dari `iOS26-main.zip`, tidak diubah (hanya nama file disesuaikan agar valid sebagai resource Android).
- `ui/icon/IosIconPack.kt`: pemetaan **package name Android -> aset** (70 package, 40 aset terpakai) dan pemuat bitmap (skala saja, rasio dijaga, tidak di-mask ulang).
- `ui/icon/IconLoader.kt`: urutan ikon: (1) ikon custom pilihan pengguna, (2) ikon iOS 26, (3) ikon asli aplikasi sebagai fallback.
- `res/raw/keep.xml`: menjaga seluruh 85 aset dari resource shrinking pada build release.
- `app/src/test/.../IosIconPackTest.kt`: tes JVM (jumlah aset, mapping, fallback).

Aplikasi tanpa entri mapping tetap memakai ikon aslinya, jadi tidak pernah muncul ikon yang salah.

## Tampilan home ala iPhone (diukur dari screenshot referensi)
- `Tokens.kt`: ikon 64dp (16,2% lebar layar), margin sisi grid 15dp, grid turun (margin atas 48dp), tinggi sel maks 102dp, label 12sp, dock 92dp tinggi dengan sudut 32dp.
- Layout awal (`LayoutStore.initialLayout`): widget Cuaca + Kalender kecil berdampingan di atas, lalu app sehari-hari (kalender, foto, kamera, jam, email, catatan, kontak, kalkulator, file, pengaturan) lebih dulu, sisanya alfabetis. Dock: Telepon, Browser, Pesan, Kamera.
- Sudah ada di ZFL: pill Search, dock kaca Liquid Glass, folder, App Library, indikator halaman.
- **Default hanya berlaku pada instalasi baru.** Jika ZFL sudah pernah dipakai di HP, layout lama tersimpan dan tidak ditimpa. Untuk melihat layout awal baru: Pengaturan > Aplikasi > ZFL > Penyimpanan > Hapus data (ini menghapus layout lama), atau pasang di HP yang belum pernah memasang ZFL. Ikon iOS 26 dan animasi langsung aktif tanpa menghapus data.
- Widget Cuaca perlu kota dipilih di Pengaturan > Weather. Label nama di bawah widget (seperti "Weather" di iPhone) belum ada.

## Animasi buka/tutup ala video
- Buka: ikon berubah menjadi permukaan membulat yang membesar ke seluruh layar (sudut ikon -> sudut layar), ikon memudar, home di belakang mengabur dan mengecil ~6%. Aplikasi di-start ~220 ms setelah tap, saat permukaan hampir penuh, lalu fade-in 140 ms.
- Tutup: saat kembali ke home, permukaan menyusut kembali ke ikon asal, home menajam kembali.
- Kode: `ui/home/AppTransition.kt`, `LauncherViewModel.appTransition`, `MainActivity.launch`/`launchThroughSurface`, `HomeScreen`; durasi di `ui/theme/Motion.kt` (`APP_*`). Memakai saklar yang sudah ada: Pengaturan > Animasi > "Open/close apps animation".
- Mengikuti skala animasi sistem; bernilai 0 (animasi mati) memakai jalur lama tanpa penundaan.
- Peluncuran tanpa posisi ikon (gestur/perintah) tetap memakai scale-up bawaan sistem.
- Timing (380/340/220 ms) adalah tebakan dari video 30 fps; perlu disetel setelah dilihat di perangkat.

## Menambah mapping
Nama file iOS memakai bundle ID iOS, bukan package Android, jadi mapping dibuat manual. Tambahkan baris di `IosIconPack.byPackage`:
```kotlin
"com.paket.android" to R.drawable.ios26_nama_aset,
```
Nama aset: huruf kecil, tanda non-alfanumerik jadi `_`, tanpa `-large` (mis. `com.apple.mobilesafari-large.png` -> `ios26_com_apple_mobilesafari`).

## Build dari HP Android (GitHub Actions)
1. Buat repo GitHub (private boleh), lalu upload seluruh isi folder project ini lewat browser HP (mode *Desktop site*) atau aplikasi git seperti Termux (`git push`). Pastikan folder `.github/workflows` ikut ter-upload.
2. Buka tab **Actions**. Workflow **CI** berjalan otomatis pada setiap push (atau pilih *CI* lalu *Run workflow*).
3. Tunggu selesai (sekitar 10-25 menit pada build pertama).
4. Buka run yang berhasil, bagian **Artifacts**, unduh **ZFL-Launcher-debug** (ZIP berisi APK).
5. Ekstrak ZIP, pasang APK (izinkan *install unknown apps*), lalu **Settings > Apps > Default apps > Home app > ZFL**.

APK **release** memerlukan keystore penandatanganan (secret `ZFL_KEYSTORE_BASE64`, `ZFL_KEYSTORE_PASSWORD`, `ZFL_KEY_ALIAS`, `ZFL_KEY_PASSWORD`) dan workflow **Release**. Untuk pemakaian pribadi, APK debug dari CI sudah cukup.

## Keterbatasan yang jujur
- Project ini **belum dikompilasi** di lingkungan pembuatan (tanpa akses internet untuk mengunduh dependensi Gradle) dan **belum diuji di perangkat**. Kompilasi pertama terjadi di GitHub Actions; jika gagal, kirim log error-nya.
- Ikon iOS 26 tidak mengikuti mode Dark/Tinted ZFL (artwork dipakai apa adanya agar tidak diubah).
- Animasi buka/tutup: dibuat ulang agar mirip video (lihat bagian bawah). Jendela aplikasi pihak ketiga yang asli tidak bisa dibuat berubah bentuk mengikuti ikon (RemoteAnimation butuh izin sistem yang tidak tersedia bagi launcher biasa di Android 14 non-root), jadi efeknya digambar oleh launcher dan aplikasi muncul dengan crossfade singkat.
