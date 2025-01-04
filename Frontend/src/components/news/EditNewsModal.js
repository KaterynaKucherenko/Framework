import React, { useEffect, useState } from 'react';
import { Modal, InputGroup, Form } from 'react-bootstrap';
import "./EditNewsModal.css";

const EditNewsModal = ({ show, handleClose, selectedNewsData, onNewsEdited}) => {
    const [title, setTitle] = useState( '');
    const [content, setContent] = useState('');
    const [tags, setTags] = useState([]);
    const [newTag, setNewTag] = useState('');
    const [modalError, setModalError] = useState('');

    const BASE_URL = 'http://localhost:8082'; 

useEffect(() => {
    if(show && selectedNewsData){
        setTitle(selectedNewsData.title );
        setContent(selectedNewsData.content );
        setTags(selectedNewsData.tags );
    }
}, [show, selectedNewsData]);


    const handleSaveChanges = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                throw new Error('User is not authenticated');
            }
           const updatedNewsData = {
                authorName: localStorage.getItem('username'), 
                content: content,
                tagNames: tags,
                title: title
           }

const response = await fetch(`${BASE_URL}/api/v1/news/${selectedNewsData.id}`, {
    method: 'PATCH',
    headers:{
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(updatedNewsData),
});

if(!response.ok){
    console.log(updatedNewsData);
    const errorData = response.headers.get('content-type')?.includes('application/json')
    ? await response.json()
    : { message: 'Error updating news' };
     throw new Error(errorData.message || 'Error creating news');
     
}




onNewsEdited();
handleClose();
} catch (err) {
setModalError(err.message);
}
};

const handleAddTag = (e) => {
    e.preventDefault();
    if(newTag.trim()) {
        setTags([...tags, newTag.trim()]);
        setNewTag('');
    }
};

const handleRemoveTag = (e, indexToRemove) => {
    e.preventDefault();
    setTags(tags.filter((_, index) => index !== indexToRemove));
};


return(
    
<Modal  show={show} onHide={handleClose} centered>
    <div className="style-modal">
<Modal.Header className="delete-borders" closeButton>
<Modal.Title>Edit News</Modal.Title>
</Modal.Header>
<Modal.Body>
{modalError && <div className="alert alert-danger">{modalError}</div>}
<Form>

<Form.Group className="mb-3 text-muted" controlId="formTitle">
    <Form.Label>TITLE</Form.Label>
    <Form.Control
    type="text"
    value={title || ''}
    onChange={(e) => setTitle(e.target.value)} />
</Form.Group>


<Form.Group className="mb-3 text-muted" controlId="formContent">
    <Form.Label >CONTENT</Form.Label>
    <Form.Control
    as="textarea"
    rows={3}
    value={content || ''}
    onChange={(e) => setContent(e.target.value)}
     />
</Form.Group>

<Form.Group className="mb-5 mp-2" controlId="formTags">
    <Form.Label className="text-muted">TAGS</Form.Label>
    <div className="mb-2">
        {tags.map((tag, index) => (
             <div key={index} className="d-inline-flex align-items-center me-2 mb-2">
<span className="btn btn-sm btn-outline-primary me-2 mb-2">{tag}</span>
<button className="btn btn-danger btn-sm" onClick={(e) => handleRemoveTag(e, index)}>X</button>
                                    </div>
                                ))}
         </div>

        
            <InputGroup className="mb-3">
                <Form.Control 
                  type="text"
                  placeholder="Enter new tag"
                  value={newTag}
                  onChange={(e) => setNewTag(e.target.value)} />
<button className="btn btn-outline-secondary" onClick={handleAddTag}>Add Tag</button>
            </InputGroup>
</Form.Group>
</Form>
</Modal.Body>
<Modal.Footer className="delete-borders">
<button  className="btn btn-outline-secondary button1 p-2 mb-4" onClick={handleClose}>Cancel</button>
 <button className="btn btn-primary button1 p-2 ms-auto mb-4" onClick={handleSaveChanges}>Save</button>         
 </Modal.Footer>        
  </div>
</Modal>
);
};

export default EditNewsModal;