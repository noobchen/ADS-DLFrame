package com.x.dtswz;

public interface OnDownloadListener {
    /**
     * @param fileId         文件的id(或包名)，用来区分哪个文件
     * @param totalSize      文件的总大小
     * @param downloadedSize 已下载的文件大小
     * @param callback       可以是项的索引
     */
    public void onDownloadProgress(String fileId, int totalSize, int downloadedSize, int callback);

    /**
     * @param state    -4暂停，-3取消，-2超时，-1失败，1下载成功，2正在下载
     * @param fileId   文件的id(或包名)，用来区分哪个文件
     * @param filePath 下载后的文件路径
     * @param callback 可以是项的索引
     */
    public void onDownloadFinished(int state, String fileId, String filePath, int callback);
}
