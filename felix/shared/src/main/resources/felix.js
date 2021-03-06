(function(global) {
  try {
    if (global instanceof DedicatedWorkerGlobalScope) {
      return;
    }
  } catch (e) {}

  global.Felix = function () {
    'use strict';
    return {
      createElementAndSetId: function(tag, id) {
        var element = document.createElement(tag);
        element.setAttribute("data-felix-id", id);
        return element;
      },
      autoPreventDefault: function (eventType, f) {
        document.addEventListener(eventType, function (event) {
          event.preventDefault();
          f(event);
        });
      },
      appendChildren: function (where, children) {
        var i, child,
            childrenLen = children.length,
            fragment = document.createDocumentFragment();
        for (i = 0; i < childrenLen; i++) {
          child = children[i];
          if (typeof child === 'string') {
            child = document.createTextNode(child);
          }
          fragment.appendChild(child);
        }
        where.appendChild(fragment);
      },
      classAdd: function (element, className) {
        element.classList.add(className)
      },
      classRemove: function (element, className) {
        element.classList.remove(className)
      },
      insertBefore: function(referenceElement, newElement) {
        referenceElement.parentNode.insertBefore(newElement, referenceElement)
      },
      insertAfter: function(referenceElement, newElement) {
        var parent = referenceElement.parentNode;
        var next = referenceElement.nextSibling;
        if (next) parent.insertBefore(newElement, next);
        else parent.appendChild(newElement);
      }
    }
  }();
})(this);
