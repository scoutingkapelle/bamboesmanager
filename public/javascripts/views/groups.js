$(document).ready(function () {

    $('#groups').on('click-row.bs.table', function (e, row) {
        window.location.href = '/groups/' + row[0]
    })
});