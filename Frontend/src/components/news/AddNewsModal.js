import React, { useState } from 'react';
import { Modal, InputGroup, Form } from 'react-bootstrap';
import "./AddNewsModal.css";

const AddNewsModal = ({ show, handleClose, onNewsAdded}) => {
    const [newTitle, setNewTitle] = useState('');
    const [newContent, setNewContent] = useState('');
    const [newTag, setNewTag] = useState('');
    const [tags, setTags] = useState([]); 
  const [isAddingTag, setIsAddingTag] = useState(false);
    const [modalError, setModalError] = useState('');

    const BASE_URL = 'http://localhost:8082'; 

    const newsData = {
    authorName: localStorage.getItem('username'), 
    content: newContent,
    tagNames: tags,
    title: newTitle,
};

    const handleCreateNews = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                throw new Error('User is not authenticated');
            }

            if (!newTitle || !newContent) {
                throw new Error('Please fill in the title and content');
            }

const response = await fetch(`${BASE_URL}/api/v1/news`, {
    method: 'POST',
    headers:{
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(newsData),
});

if(!response.ok){
    console.log(newsData);
    const errorData = response.headers.get('content-type')?.includes('application/json')
    ? await response.json()
    : { message: 'Error creating news' };
     throw new Error(errorData.message || 'Error creating news');
     
}


onNewsAdded();
handleCloseModal();
} catch (err) {
setModalError(err.message);
}
};

const handleAddTag = (e) => {
    e.preventDefault();
    if(newTag.trim()) {
        setTags([...tags, newTag.trim()]);
        setNewTag('');
        setIsAddingTag(false);
    }
};

const handleRemoveTag = (e, indexToRemove) => {
    e.preventDefault();
    setTags(tags.filter((_, index) => index !== indexToRemove));
};

const handleCloseModal = () => {
    setNewTitle('');
    setNewContent('');
    setTags([]);
    setNewTag('');
    setModalError('');
    setIsAddingTag(false);
    handleClose();
};

return(
<Modal show={show} onHide={handleCloseModal}>
    <div className="style-modal">
<Modal.Header className= "delete-borders" closeButton>
<Modal.Title>New News</Modal.Title>
</Modal.Header>
<Modal.Body>
{modalError && <div className="alert alert-danger">{modalError}</div>}
<Form>

<Form.Group className="mb-3 text-muted" controlId="formTitle">
    <Form.Label>TITLE</Form.Label>
    <Form.Control
    type="text"
    placeholder=''
    value={newTitle}
    onChange={(e) => setNewTitle(e.target.value)} />
</Form.Group>


<Form.Group className="mb-3 text-muted" controlId="formContent">
    <Form.Label >CONTENT</Form.Label>
    <Form.Control
    as="textarea"
    rows={3}
    placeholder=''
    value={newContent}
    onChange={(e) => setNewContent(e.target.value)}
     />
</Form.Group>

<Form.Group className="mb-5 mp-2" controlId="formTags">
    <Form.Label className="text-muted">TAGS</Form.Label>
    <div className="mb-2">
        {tags.map((tag, index) => (
             <div key={index} className="d-inline-flex align-items-center me-2 mb-2">
<button className="btn btn-sm btn-outline-primary me-2 mb-2">{tag}</button>
<button className="btn btn-danger btn-sm" onClick={(e) => handleRemoveTag(e, index)}>X</button>
                                    </div>
                                ))}
         </div>

         {isAddingTag ? (
            <InputGroup className="mb-3">
                <Form.Control 
                  type="text"
                  placeholder="Enter tag"
                  value={newTag}
                  onChange={(e) => setNewTag(e.target.value)} />
<button className="btn btn-outline-secondary" onClick={handleAddTag}>Add</button>
            </InputGroup>
         ) : (
            <button className="btn btn-outline-secondary button1" onClick={() => setIsAddingTag(true)}>Add Tags +</button>
         )}
</Form.Group>
</Form>
</Modal.Body>
<Modal.Footer className="delete-borders">
<button  className="btn btn-outline-secondary button1 p-2 mb-4" onClick={handleCloseModal}>Cancel</button>
 <button className="btn btn-primary button1 p-2 ms-auto mb-4" onClick={handleCreateNews}>Save </button>         
 </Modal.Footer>   </div>      
</Modal>
);
};

export default AddNewsModal;

















