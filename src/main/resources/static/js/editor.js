import UploadAdapter from './UploadAdapter.js';

export default function makeEditor(target) {
    return ClassicEditor.create(document.querySelector(target), {
        language: 'ko',
        height: 500,
        removePlugins: ["MediaEmbedToolbar"],
        extraPlugins: [ MyCustomUploadAdapterPlugin ],
    });
}


function MyCustomUploadAdapterPlugin(editor) {
    editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new UploadAdapter(loader)
    }
}