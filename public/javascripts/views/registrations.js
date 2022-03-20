$(document).ready(function () {

    $('#registrations').on('click-row.bs.table', function (e, row) {
        window.location.href = '/registrations/' + row[0]
    })
});
