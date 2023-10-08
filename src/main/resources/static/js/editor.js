import UploadAdapter from './UploadAdapter.js';

export default function makeEditor(target) {
    return ClassicEditor.create(document.querySelector(target), {
        language: 'ko',
        height: 500,
        removePlugins: ["MediaEmbedToolbar"],
        extraPlugins: [ MyCustomUploadAdapterPlugin ],
    }).then(editor => {
        const toolbarElement = editor.ui.view.toolbar.element;
        const editableElement = editor.ui.view.editable.element;
        editor.on( 'change:isReadOnly', ( evt, propertyName, isReadOnly ) => {
            if ( isReadOnly ) {
                toolbarElement.style.display = 'none';
                editableElement.style.border = 'none';
            }
        } );
        return editor;
    });
}

function MyCustomUploadAdapterPlugin(editor) {
    editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new UploadAdapter(loader)
    }
}
