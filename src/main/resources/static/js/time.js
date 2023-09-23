function timeAgo(dateString) {
    const now = new Date();
    const postDate = new Date(dateString);
    const diffInSeconds = Math.floor((now - postDate) / 1000);
    let timeAgo;

    if (diffInSeconds < 60) {
        timeAgo = diffInSeconds + '초 전';
    } else if (diffInSeconds < 3600) {
        timeAgo = Math.floor(diffInSeconds / 60) + '분 전';
    } else if (diffInSeconds < 86400) {
        timeAgo = Math.floor(diffInSeconds / 3600) + '시간 전';
    } else if (diffInSeconds < 2592000) {
        timeAgo = Math.floor(diffInSeconds / 86400) + '일 전';
    } else if (diffInSeconds < 31536000) {
        timeAgo = Math.floor(diffInSeconds / 2592000) + '달 전';
    } else {
        timeAgo = postDate.getFullYear() + '.' + (postDate.getMonth() + 1) + '.' + postDate.getDate();
    }

    return timeAgo;
}