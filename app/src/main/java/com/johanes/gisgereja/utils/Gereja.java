package com.johanes.gisgereja.utils;

/**
 * Created by j on 5/30/17.
 */

public class Gereja {
        private int _id;
        private String _name;
        private String _alamat;
        private String _deskripsi;
        private double _jarak;
        private String _longi;
        private String _lati;
        private byte[] _image;

        /*public Gereja(int id, String name, String alamat, String deskripsi, byte[] image) {
            _id = id;
            _name = name;
            _alamat = alamat;
            _deskripsi = deskripsi;
            _image = image;
        }*/
        public Gereja(){

        }


        public int getId() {
            return _id;
        }

        public String getName() {
            return _name;
        }

        public String getAlamat() {
            return _alamat;
        }

        public String getDeskripsi() {
            return _deskripsi;
        }

        public double getJarak() {
            return _jarak;
        }

        public String getLati() {
            return _lati;
        }

        public String getLongi() {
            return _longi;
        }

        public void setId(int id) {
            _id = id;
        }

        public void setName(String name) {
            _name = name;
        }

        public void setAlamat(String alamat) {
            _alamat = alamat;
        }

        public void setDeskripsi(String deskripsi) {
            _deskripsi = deskripsi;
        }

        public void setJarak(Double jarak) {
            _jarak = jarak;
        }

        public void setLati(String lati) {
            _lati = lati;
        }

        public void setLongi(String longi) {
            _longi = longi;
        }

        public byte[] getImage() {
            return _image;
        }

        public void setImage(byte[] bytes){ _image = bytes;}
}
