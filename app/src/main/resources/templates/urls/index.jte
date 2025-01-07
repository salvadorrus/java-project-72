@import hexlet.code.dto.UrlsPage
@import hexlet.code.utils.FormatedData
@import hexlet.code.utils.NamedRoutes

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        <h1>Сайты</h1>
        <table class="table table-striped table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th>ID</th>
                <th>URL</th>
                <th>Дата создания</th>
                <th>Последняя проверка</th>
                <th>Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @for(var url : page.getUrls())
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
                    <td>${FormatedData.formatedData(url.getCreatedAt())}</td>

                    @if(page.getChecks().containsKey(url.getId()))
                        <td>${FormatedData.formatedData(page.getChecks().get(url.getId()).getCreatedAt())}</td>
                        <td>${page.getChecks().get(url.getId()).getStatusCode()}</td>
                    @endif
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`
)